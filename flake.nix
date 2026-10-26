{
  description = "Minimal base image for the sbt/Scala builder image";

  # Pinned via flake.lock; update intentionally with `nix flake update`
  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";

  outputs = { self, nixpkgs }:
    let
      systems = [ "x86_64-linux" "aarch64-linux" ];
      forAll = f: nixpkgs.lib.genAttrs systems (system: f nixpkgs.legacyPackages.${system});
    in {
      packages = forAll (pkgs:
        let
          jdk = pkgs.jdk21_headless;

          # Standard glibc loader path that non-Nix binaries expect. GitHub Actions
          # mounts its own (glibc-linked) Node into job containers at /__e/node*,
          # which is what runs actions/checkout and other JavaScript actions.
          ldso =
            if pkgs.stdenv.hostPlatform.isx86_64
            then "lib64/ld-linux-x86-64.so.2"
            else "lib/ld-linux-aarch64.so.1";
        in {
          default = pkgs.dockerTools.buildLayeredImage {
            name = "nix-base";
            tag = "latest";

            contents = [
              # Everything in the image, explicitly. Add tools here (e.g. pkgs.git).
              # Merged with buildEnv: plain `contents` links each package in with
              # lndir, and openjdk's $out/bin is a symlink, which would turn the
              # whole /bin into a link to the JDK's bin and drop everything else.
              (pkgs.buildEnv {
                name = "image-root";
                pathsToLink = [ "/bin" ];
                paths = [
                  jdk
                  pkgs.bashInteractive   # sbt runner is a bash script
                  pkgs.coreutils         # uname, dirname, stty, cat, ...
                  pkgs.gnugrep
                  pkgs.gnused
                  pkgs.findutils         # find/xargs, used when trimming the sbt runner
                  pkgs.gnutar
                  pkgs.gzip
                  pkgs.unzip
                  pkgs.git               # actions/checkout clones with git if present
                ];
              })
              pkgs.dockerTools.binSh          # /bin/sh for Dockerfile RUN steps
              pkgs.dockerTools.usrBinEnv      # /usr/bin/env for the runner's shebang
              pkgs.dockerTools.caCertificates # TLS for dependency downloads
              pkgs.dockerTools.fakeNss        # /etc/passwd + /etc/group (root, nobody)
            ];

            extraCommands = ''
              # Nix images start with no /tmp at all
              mkdir -m 1777 tmp
              # nix-ld at the standard loader path: lets non-Nix binaries
              # (like the runner's Node) start, using NIX_LD* below
              mkdir -p "$(dirname ${ldso})"
              ln -s ${pkgs.nix-ld}/libexec/nix-ld ${ldso}
            '';

            config.Env = [
              "PATH=/bin"
              "JAVA_HOME=${jdk.home}"
              "SSL_CERT_FILE=/etc/ssl/certs/ca-bundle.crt"
              # Only read by nix-ld, i.e. only affects non-Nix binaries
              "NIX_LD=${pkgs.stdenv.cc.bintools.dynamicLinker}"
              "NIX_LD_LIBRARY_PATH=${pkgs.lib.makeLibraryPath [ pkgs.stdenv.cc.cc.lib pkgs.glibc ]}"
            ];
          };
        });
    };
}
