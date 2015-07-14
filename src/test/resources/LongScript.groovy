def estPremier(int nbr){
    divis = 2
    while(divis < nbr){
        if(nbr % divis == 0){
            return false
        }
        else{
            divis++
        }
    } 
    return true
}

n = 1000

currentNumber = 3
counter = 1

while(counter <= n){
    if(estPremier(currentNumber)){
       counter++
    }
    currentNumber = currentNumber + 2
}

return currentNumber