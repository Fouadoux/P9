#!/usr/bin/env sh

HOST="$1"
PORT="$2"
shift 2
CMD="$@"

echo "⏳ Attente de $HOST:$PORT..."

while ! nc -z $HOST $PORT; do
  echo "En attente..."
  sleep 1
done

echo "✅ $HOST:$PORT est prêt. Lancement de : $CMD"
exec $CMD