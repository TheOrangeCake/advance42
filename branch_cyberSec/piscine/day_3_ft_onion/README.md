docker build -t onion .
docker run --name ft_onion -p 8080:8080 -p 4242:4242 onion
<!-- docker exec -it ft_onion ash -->

ssh hoannguy@localhost -p 4242