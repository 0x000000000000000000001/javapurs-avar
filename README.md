# AVar

## JVM tests

`./bin/test` selects `avar` in the [common isolated runner](../javapurs/docs/testing.md#port-particulier), preserving this checkout and its outputs.
Use `./bin/test --help` for options and `./bin/test --clean` to rebuild the backend. The linked guide covers prerequisites, Java target/runtime settings and retained failure logs.

[![CI](https://github.com/purescript-contrib/purescript-avar/workflows/CI/badge.svg?branch=main)](https://github.com/purescript-contrib/purescript-avar/actions?query=workflow%3ACI+branch%3Amain)
[![Release](https://img.shields.io/github/release/purescript-contrib/purescript-avar.svg)](https://github.com/purescript-contrib/purescript-avar/releases)
[![Pursuit](https://pursuit.purescript.org/packages/purescript-avar/badge)](https://pursuit.purescript.org/packages/purescript-avar)
[![Maintainer: garyb](https://img.shields.io/badge/maintainer-garyb-teal.svg)](https://github.com/garyb)

Low-level interface for asynchronous variables.

## Installation

Install `avar` with [Spago](https://github.com/purescript/spago):

```sh
spago install avar
```

## Quick start

The quick start hasn't been written yet (contributions are welcome!). Usage examples are available in [the test suite](./test).

## Documentation

`avar` documentation is stored in a few places:

1. Module documentation is [published on Pursuit](https://pursuit.purescript.org/packages/purescript-avar).
2. Usage examples can be found in [the test suite](./test).

If you get stuck, there are several ways to get help:

- [Open an issue](https://github.com/purescript-contrib/purescript-avar/issues) if you have encountered a bug or problem.
- Ask general questions on the [PureScript Discourse](https://discourse.purescript.org) forum or the [PureScript Discord](https://purescript.org/chat) chat.

## Contributing

You can contribute to `avar` in several ways:

1. If you encounter a problem or have a question, please [open an issue](https://github.com/purescript-contrib/purescript-avar/issues). We'll do our best to work with you to resolve or answer it.

2. If you would like to contribute code, tests, or documentation, please [read the contributor guide](./CONTRIBUTING.md). It's a short, helpful introduction to contributing to this library, including development instructions.

3. If you have written a library, tutorial, guide, or other resource based on this package, please share it on the [PureScript Discourse](https://discourse.purescript.org)! Writing libraries and learning resources are a great way to help this library succeed.
