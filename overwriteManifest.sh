mkdir tmp
unzip target/CreatorRandomSubGraph-1.2-jar-with-dependencies.jar -d tmp
cp target/classes/META-INF/MANIFEST.MF tmp/META-INF/MANIFEST.MF
cd tmp
zip -r ../CreatorRandomSubGraph.jar .
cd ..
rm -r tmp
