#!/usr/bin/env bash
echo "<html><ul>" > change-notes.html
git log `git describe --tags --abbrev=0`..HEAD --no-merges --oneline --pretty=format:"<li>%h %s (%an)</li>" >> change-notes.html
echo "</ul></html>" >> change-notes.html

cp change-notes.html lang-fluid/src/main/resources/META-INF/
cp change-notes.html lang-typoscript/src/main/resources/META-INF/
cp change-notes.html typo3-cms/src/main/resources/META-INF/

rm change-notes.html
