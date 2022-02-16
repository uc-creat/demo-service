# syntax=docker/dockerfile:experimental
FROM adoptopenjdk/openjdk11:alpine AS build
WORKDIR /workspace/app

COPY . /workspace/app
RUN --mount=type=cache,target=/root/.gradle ./gradlew clean bootJar --stacktrace
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

FROM adoptopenjdk/openjdk11:alpine-jre

VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java","-cp","app:app/lib/*","com.tw.prograd.Application"]

