.DEFAULT_GOAL := all

all: build
	docker build -t demo/external-auth:1.0 .
.PHONY:docker

build: 
	cd java && mvn clean package
.PHONY:build
