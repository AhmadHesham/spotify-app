FROM maven
COPY . ./



COPY ./Account/target/Account-1.0-SNAPSHOT-jar-with-dependencies.jar ./Account/Account.jar
COPY ./Art/target/Art-1.0-SNAPSHOT-jar-with-dependencies.jar ./Art/Art.jar
COPY ./Auth/target/Auth-1.0-SNAPSHOT-jar-with-dependencies.jar ./Auth/Auth.jar
COPY ./Chat/target/Chat-1.0-SNAPSHOT-jar-with-dependencies.jar ./Chat/Chat.jar
COPY ./Playlist/target/Playlist-1.0-SNAPSHOT-jar-with-dependencies.jar ./Playlist/Playlist.jar
RUN mvn package

CMD ["mvn", "--version"]