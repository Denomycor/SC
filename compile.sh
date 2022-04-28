echo Compiling...
javac -sourcepath src src/*/*.java -d bin
cd bin
jar cfm Trokos.jar ../manifest.mf client/*.class exceptions/*.class network/*.class
jar cfm TrokosServer.jar ../manifest.mf model/*.class exceptions/*.class network/*.class server/*.class
mv Trokos.jar ..
mv TrokosServer.jar ..
cd ..
