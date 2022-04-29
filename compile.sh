echo Compiling...
javac -sourcepath src src/*/*.java -d bin
cd bin
jar cfm Trokos.jar ../manifestClient.mf client/*.class exceptions/*.class network/*.class
jar cfm TrokosServer.jar ../manifestServer.mf model/*.class exceptions/*.class network/*.class server/*.class
mv Trokos.jar ../client
mv TrokosServer.jar ../server
cd ..