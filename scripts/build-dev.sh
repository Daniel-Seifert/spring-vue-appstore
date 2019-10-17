cd ./src/main/resources/hm-apps && npm install && npm run build-dev && rm -rf ./../www/static/*
mkdir -p ./../www/static
cp -r ./dist/* ./../www/static
