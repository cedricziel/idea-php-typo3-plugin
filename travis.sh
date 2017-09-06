#!/bin/bash

ideaVersion="2017.2.3"
if [ "$PHPSTORM_ENV" == "2017.2.3" ]; then
    ideaVersion="2017.2"
elif [ "$PHPSTORM_ENV" == "eap" ]; then
    ideaVersion="171.4249.4"
fi

travisCache=".cache"

if [ ! -d ${travisCache} ]; then
    echo "Create cache" ${travisCache}
    mkdir ${travisCache}
fi

function download {

  url=$1
  basename=${url##*[/|\\]}
  cachefile=${travisCache}/${basename}

  if [ ! -f ${cachefile} ]; then
      wget $url -P ${travisCache};
    else
      echo "Cached file `ls -sh $cachefile` - `date -r $cachefile +'%Y-%m-%d %H:%M:%S'`"
  fi

  if [ ! -f ${cachefile} ]; then
    echo "Failed to download: $url"
    exit 1
  fi
}

# Unzip IDEA

if [ -d ./idea  ]; then
  rm -rf idea
  echo "created idea dir"
fi

if [ -d ./plugins ]; then
  rm -rf plugins
  mkdir plugins
  echo "created plugin dir"
else
  mkdir plugins
fi

if [ "$PHPSTORM_ENV" == "2017.2.3" ]; then

    #php
    download "https://download.plugins.jetbrains.com/6610/38422/php-172.4155.25.zip"
    unzip -qo $travisCache/php-172.4155.25.zip -d ./plugins

    #twig
    download "https://download.plugins.jetbrains.com/7303/35796/twig-172.2827.17.zip"
    unzip -qo $travisCache/twig-172.2827.17.zip -d ./plugins

elif [ "$PHPSTORM_ENV" == "eap" ]; then

    #php
    download "http://phpstorm.espend.de/files/proxy/phpstorm-2017.1-php.zip"
    unzip -qo $travisCache/phpstorm-2017.1-php.zip -d ./plugins

    #twig
    download "http://phpstorm.espend.de/files/proxy/phpstorm-2017.1-twig.zip"
    unzip -qo $travisCache/phpstorm-2017.1-twig.zip -d ./plugins

    # TODO: extract latest builds for plugins from eap site they are not public
    # https://confluence.jetbrains.com/display/PhpStorm/PhpStorm+Early+Access+Program
    # echo "No configuration for PhpStorm: $PHPSTORM_ENV"
    # exit 1

else
    echo "Unknown PHPSTORM_ENV value: $PHPSTORM_ENV"
    exit 1
fi

download "https://download.plugins.jetbrains.com/7320/37215/idea-php-annotation-plugin.jar"
cp $travisCache/idea-php-annotation-plugin.jar ./plugins

rm -f $travisCache/php-toolbox.jar
download "https://download.plugins.jetbrains.com/8133/36499/php-toolbox.jar"
cp $travisCache/php-toolbox.jar ./plugins

# Run the tests
if [ "$1" = "-d" ]; then
    ant -d -f build-test.xml -DIDEA_HOME=./idea
else
    ant -f build-test.xml -DIDEA_HOME=./idea
fi

# Was our build successful?
stat=$?

if [ "${TRAVIS}" != true ]; then
    ant -f build-test.xml -q clean

    if [ "$1" = "-r" ]; then
        rm -rf idea
        rm -rf plugins
    fi
fi

# Return the build status
exit ${stat}
