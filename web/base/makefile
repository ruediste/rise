UMLET_JAR=~/Umlet/umlet.jar

sourcesToImages=$(join $(addsuffix doc-files/, $(dir $(1))),  $(addsuffix .png, $(basename $(notdir $(1)))))

DIAGRAM_SOURCES=$(shell find -L -name "*.uxf" \( -path "./*" \) -print )
DIAGRAM_IMAGES=$(call sourcesToImages, ${DIAGRAM_SOURCES})

.PHONY: diagrams clean

diagrams: ${DIAGRAM_IMAGES}

clean:
	rm -f $(DIAGRAM_IMAGES)
	
# $1 - Diagram file
# $2 - png file
define T
$2: $1
	mkdir -p $(dir $2)
	java -jar $(UMLET_JAR) -action=convert -format=png -filename='$1' -output='$(basename $2)'
endef

$(foreach d, ${DIAGRAM_SOURCES}, $(eval $(call T,$d,$(call sourcesToImages,$d))))
	
	