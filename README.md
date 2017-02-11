# SteelHacks2017

##Design  

###Requirements  

####Iteration 1  
1. Use Android Phone to perform basic speech to text  
2. Parse results set for most confident result  

####Iteration 2  
1. Create buzz words to record information on  
2. Basic support for recognizing buzzwords and the value corresponding to them  
  i. Blood Pressure, BP - blood pressure reading systolic/dyastolic  
    ex. {number1} / {number2}  or {number1} over {number2}
      
  ii. Heart Rate - Beats per minute    
    ex. {number} bpm  
      
  iii. Pulse Ox - Percent of satruation of oxygen  
    ex. {number} % or percent
    
  iv. Respiration - breaths per minute    
    ex. {number} breaths per minute   
      
  v. Temperature - interal body temperature  
    ex. {number} degrees F
    
  vi. Height - patient height
    ex. {number} ft. or feet   
  
  vii. Weight - patient weight
    ex. {number} lb. or pounds  
        
#### Iteration 3  
1. Produce a summary of the buzz words recognized  
2. Show the full transcription log  
  
####Cool thoughts  
1. Be able to store the information externally (database?) and then repeat the information by the time of the next visit  
