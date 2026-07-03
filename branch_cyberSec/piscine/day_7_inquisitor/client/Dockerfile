FROM alpine:3.14

RUN apk add --no-cache lftp

RUN mkdir test
RUN echo "hello" > /test/hello.txt && \
	echo "evaluator," > /test/evaluator.key && \
	echo "we are doing an evaluation" > /test/work.md && \
	echo "at 42 Lausanne" > /test/place.hex

CMD ["sh", "-c", "while true; do lftp -u ftpuser,ftppass 10.5.0.17 -e 'mirror -R /test; ls; bye'; sleep 5; done"]
