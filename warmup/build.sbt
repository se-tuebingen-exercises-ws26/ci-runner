// Everything this project resolves ends up pre-cached in the image.
// Add any libraries or plugins your projects commonly use.
scalaVersion := "3.9.0"

libraryDependencies ++= Seq(
  "org.scalameta" %% "munit" % "1.3.6" % Test
)