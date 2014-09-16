.PHONY: all images

all: doc/index.html images

doc/index.html: doc/src/*.md $(patsubst %.uml,%.png,$(wildcard doc/src/**/*.uml))
	cd doc/src && \
	pandoc -S -s \
	-f markdown_github+pandoc_title_block+implicit_header_references+auto_identifiers\
	+multiline_tables+simple_tables+definition_lists \
	--chapters --self-contained --toc -t html -o ../index.html \
	--default-image-extension=png  \
	introduction.md \
	webFramework.md \

images: $(patsubst %.uml,%.png, $(shell find -type f -name '*.uml'))

%.png: %.uml
	java -jar plantuml.jar $<