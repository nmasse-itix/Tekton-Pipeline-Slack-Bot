# Tekton Pipeline - Approval Slack Bot

This project is a slack bot that waits for approval on a specified channel and exits upon approval.
It is used to let Product Managers approve a release into production using Tekton Pipeline.

It uses Quarkus, the Supersonic Subatomic Java Framework.
If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Pre-requisites

* Create a Slack application on [api.slack.com/apps](https://api.slack.com/apps).
* Create an **App-Level Token** with the `connections:write` scope.
* Enable **Socket Mode**.
* In **OAuth & Permissions**, enable the `app_mentions:read`, `channels:read`,`chat:write` and `commands` scopes.
* In the **Slash Commands**, add the `/lgtm` command.
* **Install App** to your Workspace

## Usage

* Invite the bot to your channel
* Start the bot with the **SLACK_CHANNEL** and **TEKTON_PIPELINE_ID** environment variables
* Wait for the bot to post the message on the channel
* Send the "/lgtm 1234" command (where 1234 is the id of the tekton pipeline to approve)
* The application exits with code 0

## Running the application in dev mode

Install Java 11:

```sh
sudo dnf install java-11-openjdk-devel
sudo alternatives --config java
sudo alternatives --config javac
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-11.0.19.0.7-1.fc37.x86_64
```

You can run your application in dev mode that enables live coding using:

```shell script
export SLACK_CHANNEL="#mad-roadshow-france-2023"
export TEKTON_PIPELINE_ID="1234"
export SLACK_BOT_TOKEN="xoxb-...."
export SLACK_APP_TOKEN="xapp-...."
./mvnw compile quarkus:dev
```

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
podman build -f src/main/docker/Dockerfile.jvm -t quay.io/madroadshowfrance2023/tekton-pipeline-slack-bot:latest .
podman login quay.io
podman push quay.io/madroadshowfrance2023/tekton-pipeline-slack-bot:latest
```

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/slack-bot-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Related Guides

* [Getting started with Bolt (Socket Mode)](https://slack.dev/java-slack-sdk/guides/getting-started-with-bolt-socket-mode)
* [Permission Scopes](https://api.slack.com/scopes)
* [Supported Web Frameworks](https://slack.dev/java-slack-sdk/guides/supported-web-frameworks)
