#!/bin/bash
javac @javac-args.txt &&
cp -r modules/net.plantabyte.cartopen/resources/* build/compile/net.plantabyte.cartopen &&
cp -r modules/net.plantabyte.cartopen.cli/resources/* build/compile/net.plantabyte.cartopen.cli &&
cp -r modules/net.plantabyte.cartopen.test/resources/* build/compile/net.plantabyte.cartopen.test
