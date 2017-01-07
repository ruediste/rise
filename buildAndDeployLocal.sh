#!/bin/sh

mvn deploy -DskipTests -Prelease -Dgpg.skip=true -Dmaven.javadoc.skip=true -pl :framework,:framework-test,:rise-es -am
