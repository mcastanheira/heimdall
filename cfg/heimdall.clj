{
  :port 3000 
  :check-interval 10 
  :database {
    :class-name "class-name" 
    :url "url" 
    :username "username" 
    :password "password"} 
  :services [
    {:uuid "1" :name "Heimdall" :ports [3000 3001] :heart-beat-url "/"}]}