test:
	./gradlew test

start: run

run:
	./gradlew bootRun

IMAGE ?= ghcr.io/titanmen1/project-devops-deploy
TAG ?= latest

PLATFORMS ?= linux/amd64

docker-build-ghcr:
	docker buildx build --platform $(PLATFORMS) -t $(IMAGE):$(TAG) --load .

docker-login-ghcr:
	echo "$$GHCR_TOKEN" | docker login ghcr.io -u titanmen1 --password-stdin

docker-push-ghcr: docker-login-ghcr docker-build-ghcr
	docker buildx build --platform $(PLATFORMS) -t $(IMAGE):$(TAG) --push .

ansible-install:
	ansible-galaxy role install -r requirements.yml

deploy:
	ansible-playbook playbook.yml --vault-password-file .vault_pass

rollback:
	ansible-playbook rollback.yml --vault-password-file .vault_pass

update-gradle:
	./gradlew wrapper --gradle-version 9.2.1

update-deps:
	./gradlew refreshVersions

install:
	./gradlew dependencies

build:
	./gradlew build

lint:
	./gradlew spotlessCheck

lint-fix:
	./gradlew spotlessApply

docker-build:
	docker build -t bulletins-app .

docker-run:
	docker run -p 8080:8080 -p 9091:9090 \
        -e SPRING_PROFILES_ACTIVE=dev \
        -e SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL} \
        -e SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME} \
        -e SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD} \
        bulletins-app

docker-stop:
	docker stop $$(docker ps -q --filter ancestor=bulletins-app)

docker-clean:
	docker rmi bulletins-app


.PHONY: build
