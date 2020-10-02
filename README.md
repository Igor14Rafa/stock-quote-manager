# Requirements
Docker, Maven, Java (>=8), The [stock-manager](https://github.com/Igor14Rafa/stock-manager) project.

# How to run

In the stock-quote-manager folder, run 

```bash
mvnw package && java -jar target/stock-quote-manager.jar
```
If you don't have the maven installed, go [here](https://maven.apache.org/install.html).
After this, you need to create the docker image

```bash
docker build -t igorrafa/stock-quote-manager .
```

You can change the image's name, but you must change it in the Dockerfile and docker-compose.yml as well.

# Usage
Just build the [stock-manager](https://github.com/Igor14Rafa/stock-manager) project and run.
## License
[MIT](https://choosealicense.com/licenses/mit/)
