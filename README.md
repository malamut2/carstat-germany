# carstat-germany

We visualize KBA's statistics on new car registrations in Germany.

**NOTE**: The project is work in progress, and does not do anything useful for now.

## How to Build and Run

First, make sure that you have the following installed on your machine:

* [Java JDK](https://adoptopenjdk.net/?variant=openjdk16&jvmVariant=hotspot), version 16 or higher
* [Maven](https://maven.apache.org/download.cgi), version 3.6.3 or higher

From the directory where this README file is contained, issue the following command:
```
mvn clean package
```

This will create a `target` folder, which contains several intermediate build artifacts and the complete application
file, named `carstat-germany-<verson>.jar`. You can now run the application with
```
java -jar target/carstat-germany-<verson>.jar
```

Once it has started up, just point your browser to [localhost:8080](http://localhost:8080).

## Technology

This project uses Java for server-side and vue.js for client-side development.
It has been set up using guidelines as published by
[Dan Vega](https://www.danvega.dev/blog/2021/01/22/full-stack-java-vue/).

## Licences

This project itself is licensed under [MIT](LICENSE). However, it uses quite a lot of open source libraries
whose exact licenses have not been reviewed by me. Before compiling/building the project for any other but
you own personal use, make sure that all relevant licenses are compatible with the proposed usage.

This application downloads and evaluates data from German authority [Kraftfahrt-Bundesamt (KBA)](https://www.kba.de/).
That data is copyrighted by KBA. Do not re-distribute that data, or any derived data, unless you have made sure
that KBA usage policies allow for it. Also, do not use or change the code in any way which would cause undue traffic
on KBA's website.
