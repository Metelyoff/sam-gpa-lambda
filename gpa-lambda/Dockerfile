FROM public.ecr.aws/sam/build-java21:latest AS builder

WORKDIR "/task"
COPY src/ src/
COPY pom.xml ./

RUN mvn -q clean install
RUN mvn dependency:copy-dependencies -DincludeScope=compile

FROM public.ecr.aws/lambda/java:21

COPY --from=builder /task/target/classes /var/task/
COPY --from=builder /task/target/dependency /var/task/lib

CMD ["robot_dreams.aws.lambdas.GPAHandler::handleRequest"]