{
  :port 3000 
  :check-interval 10 
  :timestamp-mask "dd/MM/yyyy HH:mm:ss"
  :services [
    {:uuid "1" :name "Heimdall" :ports [3000 3001] :heart-beat-url "/"}]}