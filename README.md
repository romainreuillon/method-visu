# Scala.js with sbt, Esbuild, Laminar and Echarts.js

## Production build

Make a production build with

```
$ sbt
$ runMVisu
```

To prevent cross-origin requests, run a separate terminal, and from the project root directory, run:
```
cd target/
pyhon3 -m http.server 8080
```

Then, browse: `http://localhost:8080/`
