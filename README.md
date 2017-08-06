# cljs-physics-engine

A physics engine, written in ClojureScript, built to target Node, Electron and the browser.

## Development

### Required programs

- [lein](https://leiningen.org/)
- NodeJS and npm
- Karma CLI `npm install -g karma-cli`
- Java 8

### Getting started

```
git clone git@github.com:jakehoward/cljs-physics-engine.git
npm install
```

### Testing

```
lein doo {target} {build-id} {watch-mode} # see https://github.com/bensu/doo
lein doo node test # Defaults to watch mode
lein doo node test once # If you just want to run once and exit (pretty slow for development, watcher is better for tight feedback loop)
```

#### Improvements

- Run tests in editor
- Add some color to test outputs
- Make `lein test` do something useful (CI server/all tests)
