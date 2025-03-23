# USE THIS TO AUTOMATE THE APPLICATION DEPLOYMENT

cd /deployment/online-ide-backend
git pull
docker compose -p online-compiler down
docker compose up --build -d