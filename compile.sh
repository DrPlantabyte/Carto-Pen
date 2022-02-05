#!/bin/bash
echo "Compiling..."
echo "===================="
javac @javac-args.txt &&
cp -r modules/net.plantabyte.cartopen/resources/* build/compile/net.plantabyte.cartopen &&
cp -r modules/net.plantabyte.cartopen.cli/resources/* build/compile/net.plantabyte.cartopen.cli &&
cp -r modules/net.plantabyte.cartopen.test/resources/* build/compile/net.plantabyte.cartopen.test &&
jar @jar-args_net.plantabyte.cartopen.txt &&
jar @jar-args_net.plantabyte.cartopen.app.txt &&
echo "===================="
echo "...Done."