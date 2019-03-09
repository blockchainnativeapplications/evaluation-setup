DELAY=2
MAX_RETRY=5
COUNTER=1

verifyResult() {
  if [ $1 -ne 0 ]; then
    echo "!!!!!!!!!!!!!!! "$2" !!!!!!!!!!!!!!!!"
    echo "============== ERROR =============="
    echo
    exit 1
  fi
}

executeWithRetry() {
  CMD=$*

  set -x
  eval $CMD
  res=$?
  set +x
  if [ $res -ne 0 -a $COUNTER -lt $MAX_RETRY ]; then
    COUNTER=$(expr $COUNTER + 1)
    echo "'$CMD' failed, Retry after $DELAY seconds"
    sleep $DELAY
    executeWithRetry $CMD
  else
    COUNTER=1
  fi
  verifyResult $res "After $MAX_RETRY attempts, '$CMD' failed!"
}

