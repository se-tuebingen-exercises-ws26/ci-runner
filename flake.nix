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
        in {
          default = pkgs.dockerTools.buildLayeredImage {
            name = "nix-base";
            tag = "latest";

            # Everything in the image, explicitly. Add tools here (e.g. pkgs.git).
            contents = [
              jdk
              pkgs.gitFull
              pkgs.bashInteractive   # sbt runner is a bash script
              pkgs.coreutils         # uname, dirname, stty, cat, ...
              pkgs.gnugrep
              pkgs.gnused
              pkgs.findutils         # find/xargs, used when trimming the sbt runner
              pkgs.gnutar
              pkgs.gzip
              pkgs.unzip
              pkgs.dockerTools.binSh          # /bin/sh for Dockerfile RUN steps
              pkgs.dockerTools.usrBinEnv      # /usr/bin/env for the runner's shebang
              pkgs.dockerTools.caCertificates # TLS for dependency downloads
              pkgs.dockerTools.fakeNss        # /etc/passwd + /etc/group (root, nobody)
            ];

            # Nix images start with no /tmp at all
            extraCommands = ''
              mkdir -m 1777 tmp
            '';

            config.Env = [
              "PATH=/bin"
              "JAVA_HOME=${jdk.home}"
              "SSL_CERT_FILE=/etc/ssl/certs/ca-bundle.crt"
            ];
          };
        });
    };
}
