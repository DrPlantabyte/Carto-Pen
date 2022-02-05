# Carto-Pen
Fantasy Map Making CLI tool for converting color-coded biome maps into tolkien-style SVG images

# Developer Notes

## How to Build

Just run **compile.sh**, or use the following terminal commands:

```bash
# compile the code
javac @javac-args.txt
cp -r modules/net.plantabyte.cartopen/resources/* build/compile/net.plantabyte.cartopen
cp -r modules/net.plantabyte.cartopen.cli/resources/* build/compile/net.plantabyte.cartopen.cli
cp -r modules/net.plantabyte.cartopen.test/resources/* build/compile/net.plantabyte.cartopen.test
javadoc @javadoc-args.txt
# test run
java @java-args_net.plantabyte.cartopen.test.txt
java @java-args_net.plantabyte.cartopen.app.txt
# make .jar files
jar @jar-args_net.plantabyte.cartopen.txt
jar @jar-args_net.plantabyte.cartopen.app.txt
# build CLI app with JLink
jlink-args.txt
# deploy native install package with JPackage
jpackage @jpackage-args.txt
```