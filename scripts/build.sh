cd ./src/main/resources/hm-apps && npm install && npm run build && rm -rf ./../www/static/*
mkdir -p ./../www/static
cp -r ./dist/* ./../www/static



