# Scala.js with sbt, Vite, Laminar and Plotly.js

## Install

```
$ npm install
```

## Development

Open two terminals.
In the first one, start `sbt` and, within, continuously build the Scala.js project:

```
$ sbt
...
> ~fastLinkJS
...
```

In the second one, start the Vite development server with

```
$ npm run dev
...
```

Follow the URL presented to you by Vite to open the application.

You can now continuously edit the `Main.scala` file, and Vite will automatically reload the page on save.

## Production build

Make a production build with

```
$ npm run build
```

You can then find the built files in the `dist/` directory.
You will need an HTTP server, such as `python3 -m http.server`, to open the files, as Vite rewrites `<script>` tags to prevent cross-origin requests.
