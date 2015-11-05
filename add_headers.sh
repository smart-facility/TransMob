#!/usr/bin/env bash
for i in $(find . -name '*.java') # or whatever other pattern...
do
  if ! grep -q "TransMob is free software" $i
  then
    cat copyright.java $i >$i.new && mv $i.new $i
  fi
done

for i in $(find . -name '*.sql') # or whatever other pattern...
do
  if ! grep -q "TransMob is free software" $i
  then
    cat copyright.sql $i >$i.new && mv $i.new $i
  fi
done

for i in $(find . -name '*.sh') # or whatever other pattern...
do
  if ! grep -q "TransMob is free software" $i
  then
    cat copyright.sh $i >$i.new && mv $i.new $i
  fi
done

for i in $(find . -name '*.xml') # or whatever other pattern...
do
  if ! grep -q "TransMob is free software" $i
  then
    cat copyright.xml $i >$i.new && mv $i.new $i
  fi
done

for i in $(find . -name '*.html') # or whatever other pattern...
do
  if ! grep -q "TransMob is free software" $i
  then
    cat copyright.xml $i >$i.new && mv $i.new $i
  fi
done
