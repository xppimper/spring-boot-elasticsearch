#Guide

To launch the application, you have two options
1. Create an executable JAR -> Build the Docker image with the JAR file -> Run it
2. Use the Spotify Maven Plugin and run the build

### Method 1 :

##### Create the executable JAR
`./mvnw install`

##### Build the docker image
`docker build -t projetsperso/predictice .` (feel free to modify the build name if you wish)

##### Run the image
`docker run -p 8080:8080 projetsperso/predictice`


### Method 2 :
The [Spotify Maven Plugin](https://github.com/spotify/dockerfile-maven) has not been added to the `pom.xml` so you can just run the following command

`$ mvn com.spotify:dockerfile-maven-plugin:build -Ddockerfile.repository=projetsperso/predictice`