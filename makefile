.PHONY: all images umlet

UMLET_JAR=~/Umlet/umlet.jar

all: images umlet

images: $(patsubst %.uml,%.png, $(shell find -type f -name '*.uml'))

umlet: $(patsubst %.uxf,%.png, $(shell find -type f -name '*.uxf'))

%.png: %.uml
	java -jar plantuml.jar $<
	
%.png: %.uxf
	java -jar $(UMLET_JAR) -action=convert -format=png -filename='$<' -output='$(basename $@)'
	