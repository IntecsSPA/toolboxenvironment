
Input= function(type) {


    this.init= function(){
        switch (this.Type){
            case "BoundingBoxData":
                this.TypeInformation= new BoundingBoxDataInfo();
                break;
            
            case "LiteralData":
                this.TypeInformation= new LiteralDataInfo();
                break;
                
            case "ComplexData":
                this.TypeInformation= new ComplexDataInfo();
                break;    
        } 
    };
     
    /*Return the xml element of the WPS processing Input*/
    this.addInputElement= function(describeDocument){ 
        var inputsNodeList=describeDocument.getElementsByTagName("Input");
        var createInput=true;
        var elementInfo;
        for(var i=0; i<inputsNodeList.length;i++){
            var identifierElement=describeDocument.getElementsByTagName("ows:Identifier")[0];
            if(identifierElement.textContent == this.Identifier){
                createInput=false;
                break;
            }
        }
        if(createInput)
            elementInfo=describeDocument.createElement ("Input");
        else
            elementInfo=inputsNodeList[i];
        
        elementInfo.setAttribute("minOccurs", this.MinOccurs);
        elementInfo.setAttribute("maxOccurs", this.MaxOccurs);
        
        this.addInputInfo(describeDocument,elementInfo);
        
        this.addInputTypeInformation(describeDocument,elementInfo);
        
        var processNode = describeDocument.getElementsByTagName("DataInputs")[0];
        
        processNode.appendChild(elementInfo);
    };
    
    this.addInputInfo=function(describeProcessDoc,inputNodeEl){

        var elInfo=inputNodeEl.getElementsByTagName("ows:Identifier")[0];
        if(!elInfo)
            elInfo=describeProcessDoc.createElement ("ows:Identifier");
        elInfo.textContent=this.Identifier;
        
        inputNodeEl.appendChild (elInfo);
        
        elInfo=inputNodeEl.getElementsByTagName("ows:Title")[0];
        if(!elInfo)
            elInfo=describeProcessDoc.createElement ("ows:Title");
        elInfo.textContent=this.Title;
        inputNodeEl.appendChild (elInfo);
        
        elInfo=inputNodeEl.getElementsByTagName("ows:Abstract")[0];
        if(!elInfo)
            elInfo=describeProcessDoc.createElement ("ows:Abstract");
        elInfo.textContent=this.Abstract;
        inputNodeEl.appendChild (elInfo);
        
        var metadata=inputNodeEl.getElementsByTagName("ows:Metadata");
        for(var i=0; i<metadata.length;i++){
            inputNodeEl.removeChild(metadata[i]);
        }
        if(this.Metadata){
            if(this.Metadata.indexOf(",")!=-1){
                metadata=this.Metadata.split(","); 
                for(i=0; i<metadata.length;i++){
                    elInfo=describeProcessDoc.createElement("ows:Metadata");
                    elInfo.setAttribute("xlink:title",metadata[i]);
                    inputNodeEl.appendChild (elInfo);
                }    
            }else
            if(this.Metadata!=""){
                elInfo=describeProcessDoc.createElement("ows:Metadata");
                elInfo.setAttribute("xlink:title",this.Metadata);
                inputNodeEl.appendChild (elInfo);
            }
        } 

    };
    
    
    this.addInputTypeInformation= function(describeProcessDoc,inputNodeEl){
        switch (this.Type){
            case "BoundingBoxData":
                this.addInputBoundingBoxDataInformation(describeProcessDoc,inputNodeEl);
                break;
            
            case "LiteralData":
                this.addInputLiteralDataInformation(describeProcessDoc,inputNodeEl);
                break;
                
            case "ComplexData":
                this.addInputComplexDataInformation(describeProcessDoc,inputNodeEl);
                break;    
        }
        
    };
    
    this.addInputBoundingBoxDataInformation = function (describeProcessDoc,inputNodeEl){
        var bboxElement=describeProcessDoc.createElement ("BoundingBoxData");
        var crsElement=null;
        var defualtElement= describeProcessDoc.createElement ("Default");
        var supportElement= describeProcessDoc.createElement ("Supported");
        for(var i=0; i< this.TypeInformation.CRSSupported.length; i++){
            crsElement= describeProcessDoc.createElement ("CRS");
            crsElement.textContent=this.TypeInformation.CRSSupported[i];
            supportElement.appendChild(crsElement);
        }
        crsElement= describeProcessDoc.createElement ("CRS");
        crsElement.textContent=this.TypeInformation.CRSSupported[this.TypeInformation.crsDefualtIndex];
        defualtElement.appendChild(crsElement); 
        bboxElement.appendChild(defualtElement);
        bboxElement.appendChild(supportElement);
        inputNodeEl.appendChild(bboxElement);
    };
    
    this.addInputLiteralDataInformation = function (describeProcessDoc,inputNodeEl){
        var literalElement=describeProcessDoc.createElement ("LiteralData");
        var i;
        var allowedValuesElement=null;
        var defualtElement=null, supportElement=null;
        if(this.TypeInformation.DataType){
            var dataTypeElement=describeProcessDoc.createElement ("ows:DataType");
            dataTypeElement.setAttribute("ows:reference" , "http://www.w3.org/TR/xmlschema-2/#"+this.TypeInformation.DataType);
            dataTypeElement.textContent=this.TypeInformation.DataType;
            literalElement.appendChild(dataTypeElement);
        }
       
        if(this.TypeInformation.UOMsSupported.length > 0){
            var uomsElement=describeProcessDoc.createElement ("UOMs");
            defualtElement= describeProcessDoc.createElement ("Default");
            supportElement= describeProcessDoc.createElement ("Supported");
            var uomElement=null;
            for(i=0; i< this.TypeInformation.UOMsSupported.length; i++){
                uomElement=describeProcessDoc.createElement ("ows:UOM");
                uomElement.textContent=this.TypeInformation.UOMsSupported[i];
                supportElement.appendChild(uomElement);
            }
            uomElement= describeProcessDoc.createElement ("ows:UOM");
            uomElement.textContent=this.TypeInformation.UOMsSupported[this.TypeInformation.uomDefualtIndex];
            defualtElement.appendChild(uomElement); 
            uomsElement.appendChild(defualtElement); 
            uomsElement.appendChild(supportElement);
            literalElement.appendChild(uomsElement);
        }
       
       
        if(this.TypeInformation.AllowedValues.length > 0){
            allowedValuesElement=describeProcessDoc.createElement ("ows:AllowedValues");
            var valueElement=null;
            for(i=0; i< this.TypeInformation.AllowedValues.length; i++){
                valueElement=describeProcessDoc.createElement ("ows:Value");
                valueElement.textContent=this.TypeInformation.AllowedValues[i];
                allowedValuesElement.appendChild(valueElement);
            }
            literalElement.appendChild(allowedValuesElement); 
        }else{
            var anyValueElement=describeProcessDoc.createElement ("ows:AnyValue"); 
            literalElement.appendChild(anyValueElement); 
        }
       
        if(this.TypeInformation.AllowedRanges.length > 0){
            allowedValuesElement=literalElement.describeDocument.getElementsByTagName("ows:AllowedValues")[0];
            if(!allowedValuesElement)
                allowedValuesElement=describeProcessDoc.createElement ("ows:AllowedValues");
            var rangeElement=null, minValueElement=null, maxValueElement=null; 
            for(i=0; i< this.TypeInformation.AllowedRanges.length; i++){
                rangeElement=describeProcessDoc.createElement ("ows:Range");
                minValueElement=describeProcessDoc.createElement ("ows:MinimumValue");
                minValueElement.textContent=this.TypeInformation.AllowedRanges[i].minValue;
                maxValueElement=describeProcessDoc.createElement ("ows:MaximumValue");
                maxValueElement.textContent=this.TypeInformation.AllowedRanges[i].maxValue;
                rangeElement.appendChild(minValueElement);
                rangeElement.appendChild(maxValueElement);
                allowedValuesElement.appendChild(rangeElement);
            }
            literalElement.appendChild(allowedValuesElement);  
        }
       
        if(this.TypeInformation.DefaultValue){
            var defaultValueElement=describeProcessDoc.createElement("DefaultValue");
            defaultValueElement.textContent=this.TypeInformation.DefaultValue;
            literalElement.appendChild(defaultValueElement);  
        }
       
        inputNodeEl.appendChild(literalElement);
    };
    
    
    this.addInputComplexDataInformation = function (describeProcessDoc,inputNodeEl){
        var complexDataElement=describeProcessDoc.createElement ("ComplexData");
        var defualtElement= describeProcessDoc.createElement ("Default");
        var supportElement= describeProcessDoc.createElement ("Supported");
        if(this.TypeInformation.MaximumMegabytes){
            complexDataElement.setAttribute("maximumMegabytes", this.TypeInformation.MaximumMegabytes);
        }
       
        var formatElement, mimeTypeElement, encodingElement, schemaElement;
        for(var i=0; i< this.TypeInformation.formatSupported.length; i++){
            formatElement= describeProcessDoc.createElement ("Format");
            mimeTypeElement = describeProcessDoc.createElement ("MimeType");
            mimeTypeElement.textContent= this.TypeInformation.formatSupported[i].mimeType;
            formatElement.appendChild(mimeTypeElement);
           
            if(this.TypeInformation.formatSupported[i].encoding){
                encodingElement = describeProcessDoc.createElement ("Encoding");
                encodingElement.textContent= this.TypeInformation.formatSupported[i].encoding;
                formatElement.appendChild(encodingElement); 
            }
           
            if(this.TypeInformation.formatSupported[i].schema){
                schemaElement = describeProcessDoc.createElement ("Schema");
                schemaElement.textContent= this.TypeInformation.formatSupported[i].schema;
                formatElement.appendChild(schemaElement); 
            }
            supportElement.appendChild(formatElement);  
        }
       
        formatElement= describeProcessDoc.createElement ("Format");
        mimeTypeElement = describeProcessDoc.createElement ("MimeType");
        mimeTypeElement.textContent= this.TypeInformation.formatSupported[this.TypeInformation.formatDefualtIndex].mimeType;
        formatElement.appendChild(mimeTypeElement); 
        
        if(this.TypeInformation.formatSupported[this.TypeInformation.formatDefualtIndex].encoding){
            encodingElement = describeProcessDoc.createElement ("Encoding");
            encodingElement.textContent= this.TypeInformation.formatSupported[this.TypeInformation.formatDefualtIndex].encoding;
            formatElement.appendChild(encodingElement); 
        }
           
        if(this.TypeInformation.formatSupported[this.TypeInformation.formatDefualtIndex].schema){
            schemaElement = describeProcessDoc.createElement ("Schema");
            schemaElement.textContent= this.TypeInformation.formatSupported[this.TypeInformation.formatDefualtIndex].schema;
            formatElement.appendChild(schemaElement); 
        }
       
        defualtElement.appendChild(formatElement);
        
        complexDataElement.appendChild(defualtElement);
        complexDataElement.appendChild(supportElement);
        inputNodeEl.appendChild(complexDataElement);
    };
    
    
    this.setInputInformationByElement = function(inputElement){
        var minOccurs=inputElement.getAttribute("minOccurs");
        if(minOccurs)
           this.MinOccurs=minOccurs; 
        var maxOccurs=inputElement.getAttribute("maxOccurs");
        if(maxOccurs)
           this.MaxOccurs=maxOccurs;
       
        var elInfo=inputElement.getElementsByTagName("Identifier")[0];
        if(elInfo)
            this.Identfier=elInfo.textContent; 
        
        elInfo=inputElement.getElementsByTagName("Title")[0];
        if(elInfo)
            this.Title=elInfo.textContent; 
       
        elInfo=inputElement.getElementsByTagName("Abstract")[0];
        if(elInfo)
            this.Abstract=elInfo.textContent;
                
        switch (this.Type){
            case "BoundingBoxData":
                this.setInputBoundingBoxDataInformationFromElement(inputElement);
                break;
            
            case "LiteralData":
                this.setInputLiteralDataInformationFromElement(inputElement);
                break;
                
            case "ComplexData":
                this.setInputComplexDataInformationFromElement(inputElement);
                break;    
        }
    };
    
    this.setInputBoundingBoxDataInformationFromElement= function(inputBBOXElement){
        var bboxElement=inputBBOXElement.getElementsByTagName ("BoundingBoxData")[0];
        var defualtElement=bboxElement.getElementsByTagName ("Default")[0];
        var supportElement=bboxElement.getElementsByTagName ("Supported")[0];
        var defaultCRS= defualtElement.getElementsByTagName ("CRS")[0].textContent; 
        var supportedCRSArray=supportElement.getElementsByTagName ("CRS");
        for(var i=0; i<supportedCRSArray.length; i++ ){
            this.TypeInformation.CRSSupported.push(supportedCRSArray[i].textContent);
            if(supportedCRSArray[i].textContent == defaultCRS)
                this.TypeInformation.crsDefualtIndex=i;
        }
        
    };
    
    this.setInputComplexDataInformationFromElement= function(inputComplexDataElement){
        var complexDataElement=inputComplexDataElement.getElementsByTagName ("ComplexData")[0];
        var maximumMegabytes=complexDataElement.getAttribute("maximumMegabytes");
        if(maximumMegabytes)
           this.TypeInformation.setMaximumMegabytes(maximumMegabytes);
        var defualtElement=complexDataElement.getElementsByTagName ("Default")[0];
        var supportElement=complexDataElement.getElementsByTagName ("Supported")[0];
        var defaultFormat= defualtElement.getElementsByTagName ("Format")[0]; 
        var supportedFormatArray=supportElement.getElementsByTagName ("Format");
        var schema=null,encoding=null,mimeType=null, infoElements;
        var defaultMimeType=defaultFormat.getElementsByTagName ("MimeType")[0].textContent;
        for(var i=0; i<supportedFormatArray.length; i++ ){
            infoElements=supportedFormatArray[i].getElementsByTagName ("MimeType");
            if(infoElements.length >0){
                mimeType=infoElements[0].textContent;
            }
            infoElements=supportedFormatArray[i].getElementsByTagName ("Encoding");
            if(infoElements.length >0){
                encoding=infoElements[0].textContent;
            }
            infoElements=supportedFormatArray[i].getElementsByTagName ("Schema");
            if(infoElements.length >0){
                schema=infoElements[0].textContent;
            }
            this.TypeInformation.addFormat(mimeType, encoding, schema);
            if(mimeType == defaultMimeType)
                this.TypeInformation.formatDefualtIndex=i;
        }
    };
    
    this.setInputLiteralDataInformationFromElement= function(inputLiteralDataElement){
        var literalDataElement=inputLiteralDataElement.getElementsByTagName ("LiteralData")[0];
        var defaultElement,supportElement, uomElements, uomDefault,valueElements;
        var arrayElements=literalDataElement.getElementsByTagName ("UOMs");
        if(arrayElements.length > 0){
             defaultElement=arrayElements[0].getElementsByTagName ("Default")[0];
             supportElement=arrayElements[0].getElementsByTagName ("Supported")[0];
             uomDefault=defaultElement.getElementsByTagName ("UOM")[0].texContent;
             uomElements=supportElement.getElementsByTagName ("UOM");
             for(var i=0; i<uomElements.length; i++ ){
                 this.TypeInformation.addUOM(uomElements[i].textContent);
                 if(uomElements[i].textContent == uomDefault)
                    this.TypeInformation.uomDefualtIndex=i; 
             }
        }
        arrayElements=literalDataElement.getElementsByTagName ("AllowedValues");
        if(arrayElements.length > 0){
             valueElements=arrayElements[0].getElementsByTagName ("Value");
             for(i=0; i<valueElements.length; i++ ){
                 this.TypeInformation.addAllowedValue(valueElements[i].textContent);
             }
        }
        arrayElements=literalDataElement.getElementsByTagName ("DataType");
        if(arrayElements.length > 0){
             this.TypeInformation.setDataType(arrayElements[0].textContent);
        }
        arrayElements=literalDataElement.getElementsByTagName ("DefaultValue");
        if(arrayElements.length > 0){
             this.TypeInformation.setDefaultValue(arrayElements[0].textContent);
        }
    };
    
    // COMMON INPUT
    this.Identifier = null; // String
    this.Title = null ;
    this.Abstract = null ;
    this.Metadata= null;
    this.MinOccurs = null ; // int
    this.MaxOccurs = null ; // int

    this.Type = type;
    
    this.TypeInformation= null;

    this.getRecordType = function(){  // vanno aggiunti tutti i campi che vengono compilati dall utente

        return([
        {
            name:'Identifier'
        },{
            name:'Type'
        },{
            name:'Title'
        }
        ]);
    }
    
    this.init();
}; 

Output = function(type){

    this.init = function(){
        switch (this.Type){
            case "BoundingBoxOutput":
                this.TypeInformation= new BoundingBoxDataInfo();
                break;
            
            case "LiteralOutput":
                this.TypeInformation= new LiteralDataInfo();
                break;
                
            case "ComplexOutput":
                this.TypeInformation= new ComplexDataInfo();
                break;    
        } 
    };
    
    /*Return the xml element of the WPS processing Output*/
    this.addOutputElement= function(describeDocument){ 
        var outputsNodeList=describeDocument.getElementsByTagName("Output");
        var createOutput=true;
        var elementInfo;
        for(var i=0; i<outputsNodeList.length;i++){
            var identifierElement=describeDocument.getElementsByTagName("ows:Identifier")[0];
            if(identifierElement.textContent == this.Identifier){
                createOutput=false;
                break;
            }
        }
        if(createOutput)
            elementInfo=describeDocument.createElement ("Output");
        else
            elementInfo=outputsNodeList[i];
        
        
        this.addOutputInfo(describeDocument,elementInfo);
        
        this.addOutputTypeInformation(describeDocument,elementInfo);
        
        var processNode = describeDocument.getElementsByTagName("ProcessOutputs")[0];
        
        processNode.appendChild(elementInfo);
    };
    
    this.addOutputInfo=function(describeProcessDoc,outputNodeEl){

        var elInfo=outputNodeEl.getElementsByTagName("ows:Identifier")[0];
        if(!elInfo)
            elInfo=describeProcessDoc.createElement ("ows:Identifier");
        elInfo.textContent=this.Identifier;
        
        outputNodeEl.appendChild (elInfo);
        
        elInfo=outputNodeEl.getElementsByTagName("ows:Title")[0];
        if(!elInfo)
            elInfo=describeProcessDoc.createElement ("ows:Title");
        elInfo.textContent=this.Title;
        outputNodeEl.appendChild (elInfo);
        
        elInfo=outputNodeEl.getElementsByTagName("ows:Abstract")[0];
        if(!elInfo)
            elInfo=describeProcessDoc.createElement ("ows:Abstract");
        elInfo.textContent=this.Abstract;
        outputNodeEl.appendChild (elInfo);
        
        var metadata=outputNodeEl.getElementsByTagName("ows:Metadata");
        for(var i=0; i<metadata.length;i++){
            outputNodeEl.removeChild(metadata[i]);
        }
        if(this.Metadata){
            if(this.Metadata.indexOf(",")!=-1){
                metadata=this.Metadata.split(","); 
                for(i=0; i<metadata.length;i++){
                    elInfo=describeProcessDoc.createElement("ows:Metadata");
                    elInfo.setAttribute("xlink:title",metadata[i]);
                    outputNodeEl.appendChild (elInfo);
                }    
            }else
            if(this.Metadata!=""){
                elInfo=describeProcessDoc.createElement("ows:Metadata");
                elInfo.setAttribute("xlink:title",this.Metadata);
                outputNodeEl.appendChild (elInfo);
            }
        } 

    };
    
    this.addOutputTypeInformation= function(describeProcessDoc,outputNodeEl){
        switch (this.Type){
            case "BoundingBoxOutput":
                this.addOutputBoundingBoxDataInformation(describeProcessDoc,outputNodeEl);
                break;
            
            case "LiteralOutput":
                this.addOutputLiteralDataInformation(describeProcessDoc,outputNodeEl);
                break;
                
            case "ComplexOutput":
                this.addOutputComplexDataInformation(describeProcessDoc,outputNodeEl);
                break;    
        }
        
    };
    
    this.addOutputBoundingBoxDataInformation = function (describeProcessDoc,outputNodeEl){
        var bboxElement=describeProcessDoc.createElement ("BoundingBoxOutput");
        var crsElement=null;
        var defualtElement= describeProcessDoc.createElement ("Default");
        var supportElement= describeProcessDoc.createElement ("Supported");
        for(var i=0; i< this.TypeInformation.CRSSupported.length; i++){
            crsElement= describeProcessDoc.createElement ("CRS");
            crsElement.textContent=this.TypeInformation.CRSSupported[i];
            supportElement.appendChild(crsElement);
        }
        crsElement= describeProcessDoc.createElement ("CRS");
        crsElement.textContent=this.TypeInformation.CRSSupported[this.TypeInformation.crsDefualtIndex];
        defualtElement.appendChild(crsElement); 
        bboxElement.appendChild(defualtElement);
        bboxElement.appendChild(supportElement);
        outputNodeEl.appendChild(bboxElement);
    };
    
    
    this.addOutputLiteralDataInformation = function (describeProcessDoc,outputNodeEl){
        var literalElement=describeProcessDoc.createElement ("LiteralOutput");
        var i;
        var defualtElement=null, supportElement=null;
        if(this.TypeInformation.DataType){
            var dataTypeElement=describeProcessDoc.createElement ("ows:DataType");
            dataTypeElement.setAttribute("ows:reference" , "http://www.w3.org/TR/xmlschema-2/#"+this.TypeInformation.DataType);
            dataTypeElement.textContent=this.TypeInformation.DataType;
            literalElement.appendChild(dataTypeElement);
        }
       
        if(this.TypeInformation.UOMsSupported.length > 0){
            var uomsElement=describeProcessDoc.createElement ("UOMs");
            defualtElement= describeProcessDoc.createElement ("Default");
            supportElement= describeProcessDoc.createElement ("Supported");
            var uomElement=null;
            for(i=0; i< this.TypeInformation.UOMsSupported.length; i++){
                uomElement=describeProcessDoc.createElement ("ows:UOM");
                uomElement.textContent=this.TypeInformation.UOMsSupported[i];
                supportElement.appendChild(uomElement);
            }
            uomElement= describeProcessDoc.createElement ("ows:UOM");
            uomElement.textContent=this.TypeInformation.UOMsSupported[this.TypeInformation.uomDefualtIndex];
            defualtElement.appendChild(uomElement); 
            uomsElement.appendChild(defualtElement); 
            uomsElement.appendChild(supportElement);
            literalElement.appendChild(uomsElement);
        }
       
        outputNodeEl.appendChild(literalElement);
    };
    
    this.addOutputComplexDataInformation = function (describeProcessDoc,outputNodeEl){
        var complexDataElement=describeProcessDoc.createElement ("ComplexOutput");
        if(this.TypeInformation.MaximumMegabytes){
            complexDataElement.setAttribute("maximumMegabytes", this.TypeInformation.MaximumMegabytes);
        }
        var defualtElement= describeProcessDoc.createElement ("Default");
        var supportElement= describeProcessDoc.createElement ("Supported");

        var formatElement, mimeTypeElement, encodingElement, schemaElement;
        for(var i=0; i< this.TypeInformation.formatSupported.length; i++){
            formatElement= describeProcessDoc.createElement ("Format");
            mimeTypeElement = describeProcessDoc.createElement ("MimeType");
            mimeTypeElement.textContent= this.TypeInformation.formatSupported[i].mimeType;
            formatElement.appendChild(mimeTypeElement);
           
            if(this.TypeInformation.formatSupported[i].encoding){
                encodingElement = describeProcessDoc.createElement ("Encoding");
                encodingElement.textContent= this.TypeInformation.formatSupported[i].encoding;
                formatElement.appendChild(encodingElement); 
            }
           
            if(this.TypeInformation.formatSupported[i].schema){
                schemaElement = describeProcessDoc.createElement ("Schema");
                schemaElement.textContent= this.TypeInformation.formatSupported[i].schema;
                formatElement.appendChild(schemaElement); 
            }
            supportElement.appendChild(formatElement);  
        }
       
        formatElement= describeProcessDoc.createElement ("Format");
        mimeTypeElement = describeProcessDoc.createElement ("MimeType");
        mimeTypeElement.textContent= this.TypeInformation.formatSupported[this.TypeInformation.formatDefualtIndex].mimeType;
        formatElement.appendChild(mimeTypeElement); 
        
        if(this.TypeInformation.formatSupported[this.TypeInformation.formatDefualtIndex].encoding){
            encodingElement = describeProcessDoc.createElement ("Encoding");
            encodingElement.textContent= this.TypeInformation.formatSupported[this.TypeInformation.formatDefualtIndex].encoding;
            formatElement.appendChild(encodingElement); 
        }
           
        if(this.TypeInformation.formatSupported[this.TypeInformation.formatDefualtIndex].schema){
            schemaElement = describeProcessDoc.createElement ("Schema");
            schemaElement.textContent= this.TypeInformation.formatSupported[this.TypeInformation.formatDefualtIndex].schema;
            formatElement.appendChild(schemaElement); 
        }
       
        defualtElement.appendChild(formatElement);
        
        complexDataElement.appendChild(defualtElement);
        complexDataElement.appendChild(supportElement);
        outputNodeEl.appendChild(complexDataElement);
    };
    
    
    this.setOutputInformationByElement = function(outputElement){

        var elInfo=outputElement.getElementsByTagName("Identifier")[0];
        if(elInfo)
            this.Identfier=elInfo.textContent; 
        
        elInfo=outputElement.getElementsByTagName("Title")[0];
        if(elInfo)
            this.Title=elInfo.textContent; 
       
        elInfo=outputElement.getElementsByTagName("Abstract")[0];
        if(elInfo)
            this.Abstract=elInfo.textContent;
                
        switch (this.Type){
            case "BoundingBoxOutput":
                this.setOutputBoundingBoxOutputInformationFromElement(outputElement);
                break;
            
            case "LiteralOutput":
                this.setOutputLiteralOutputInformationFromElement(outputElement);
                break;
                
            case "ComplexOutput":
                this.setOutputComplexOutputInformationFromElement(outputElement);
                break;    
        }
    };
    
    this.setOutputBoundingBoxOutputInformationFromElement= function(outputBBOXElement){
        var bboxElement=outputBBOXElement.getElementsByTagName ("BoundingBoxData")[0];
        var defaultElement=bboxElement.getElementsByTagName ("Default")[0];
        var supportElement=bboxElement.getElementsByTagName ("Supported")[0];
        var defaultCRS= defaultElement.getElementsByTagName ("CRS")[0].textContent; 
        var supportedCRSArray=supportElement.getElementsByTagName ("CRS");
        for(var i=0; i<supportedCRSArray.length; i++ ){
            this.TypeInformation.CRSSupported.push(supportedCRSArray[i].textContent);
            if(supportedCRSArray[i].textContent == defaultCRS)
                this.TypeInformation.crsDefualtIndex=i;
        }
        
    };
    
    this.setOutputComplexOutputInformationFromElement= function(outputComplexOutputElement){
        var complexOutputElement=outputComplexOutputElement.getElementsByTagName ("ComplexOutput")[0];
        var maximumMegabytes=complexOutputElement.getAttribute("maximumMegabytes");
        if(maximumMegabytes)
           this.TypeInformation.setMaximumMegabytes(maximumMegabytes);
        var defualtElement=complexOutputElement.getElementsByTagName ("Default")[0];
        var supportElement=complexOutputElement.getElementsByTagName ("Supported")[0];
        var defaultFormat= defualtElement.getElementsByTagName ("Format")[0]; 
        var supportedFormatArray=supportElement.getElementsByTagName ("Format");
        var schema=null,encoding=null,mimeType=null, infoElements;
        var defaultMimeType=defaultFormat.getElementsByTagName ("MimeType")[0].textContent;
        for(var i=0; i<supportedFormatArray.length; i++ ){
            infoElements=supportedFormatArray[i].getElementsByTagName ("MimeType");
            if(infoElements.length >0){
                mimeType=infoElements[0].textContent;
            }
            infoElements=supportedFormatArray[i].getElementsByTagName ("Encoding");
            if(infoElements.length >0){
                encoding=infoElements[0].textContent;
            }
            infoElements=supportedFormatArray[i].getElementsByTagName ("Schema");
            if(infoElements.length >0){
                schema=infoElements[0].textContent;
            }
            this.TypeInformation.addFormat(mimeType, encoding, schema);
            if(mimeType == defaultMimeType)
                this.TypeInformation.formatDefualtIndex=i;
        }
    };
    
    this.setOutputLiteralOutputInformationFromElement= function(outputLiteralOutputElement){
        var literalOutputElement=outputLiteralOutputElement.getElementsByTagName ("LiteralOutput")[0];
        var defaultElement,supportElement, uomElements, uomDefault;
        var arrayElements=literalOutputElement.getElementsByTagName ("UOMs");
        if(arrayElements.length > 0){
             defaultElement=arrayElements[0].getElementsByTagName ("Default")[0];
             supportElement=arrayElements[0].getElementsByTagName ("Supported")[0];
             uomDefault=defaultElement.getElementsByTagName ("UOM")[0].texContent;
             uomElements=supportElement.getElementsByTagName ("UOM");
             for(var i=0; i<uomElements.length; i++ ){
                 this.TypeInformation.addUOM(uomElements[i].textContent);
                 if(uomElements[i].textContent == uomDefault)
                    this.TypeInformation.uomDefualtIndex=i; 
             }
        } 
        arrayElements=literalOutputElement.getElementsByTagName ("DataType");
        if(arrayElements.length > 0){
             this.TypeInformation.setDataType(arrayElements[0].textContent);
        }
    };
    
    // COMMON OUTPUT
    this.Identifier =null ; // String
    this.Title = null;
    this.Abstract = null ;
    this.Metadata= null;

    this.Type = type;
    
    this.TypeInformation= null;

    this.getRecordType = function(){  // vanno aggiunti tutti i campi che vengono compilati dall utente

        return([
        {
            name:'Identifier'
        },{
            name:'Type'
        },{
            name:'Title'
        }
        ]);
    }

    this.init();
};

DescribeObject = function(describeProcessString){
    
    this.init = function(describeProcessString){
        if(describeProcessString){
            this.parseXMLDescribeProcess(describeProcessString); 
        }     
    };
       
    this.Identifier= null;
    
    this.Title= null;
    
    this.Abstract= null;
    
    this.Metadata= null;
    
    this.processVersion = null;
    
    this.storeSupported = false;
    
    this.statusSupported = false;
    
    this.describeProdcessDoc= null;
    
    this.inputs = new Array();

    this.outputs = new Array();

    this.insertInput = function(input){
        this.inputs.push(input);
    }

    this.insertOutput = function(output) {
        this.outputs.push(output);
    }

    this.updateInput= function(inputObj){
        
        for(var i=0; i<this.inputs.length; i++){
            if(inputObj.Identifier == this.inputs[i].Identifier){
                this.inputs[i]=inputObj;
            }
            
        }
        
    };
    
    this.updateOutput= function(outputObj){
        for(var i=0; i<this.outputs.length; i++){
            if(outputObj.Identifier == this.outputs[i].Identifier){
                this.outputs[i]=outputObj;
            }
        }    
    };
    
    this.removeInput = function (inputIdentifer){
        var newInputsArray= new Array();
        for(var i=0; i<this.inputs.length; i++){
            if(inputIdentifer != this.inputs[i].Identifier){
                newInputsArray.push(this.inputs[i]);
            }
        }
        this.inputs=newInputsArray;  
    };
    
    this.removeOutput = function (outputIdentifer){
        var newOutputsArray= new Array();
        for(var i=0; i<this.outputs.length; i++){
            if(outputIdentifer != this.outputs[i].Identifier){
                newOutputsArray.push(this.outputs[i]);
            }
        }
        this.outputs=newOutputsArray;  
    };
    
    this.getDescribeProcess = function(){
        if (document.implementation.createDocument && 
            document.implementation.createDocumentType){
                
            this.describeProdcessDoc = document.implementation.createDocument("","ProcessDescriptions",null);
          
            var rootEl=  this.describeProdcessDoc.getElementsByTagName("ProcessDescriptions")[0];
            rootEl.setAttribute ("service" , "WPS");
            rootEl.setAttribute ("version" , "1.0.0");
            rootEl.setAttribute ("xml:lang" , "en-UK");
            rootEl.setAttribute ("xmlns:wps" , "http://www.opengis.net/wps/1.0.0");
            var processNode = this.describeProdcessDoc.createElement ("ProcessDescription");
            processNode.setAttribute ("xmlns:ows" , "http://www.opengis.net/ows/1.1");
            processNode.setAttribute ("xmlns:xlink" , "http://www.w3.org/1999/xlink");
            processNode.setAttribute ("wps:processVersion" , this.processVersion);
            processNode.setAttribute ("storeSupported" , this.storeSupported);
            processNode.setAttribute ("statusSupported" , this.statusSupported);
            
            this.describeProdcessDoc.documentElement.appendChild (processNode);
                
            this.addProcessingInfoElements();
            this.addInputsElements();
            this.addOutputsElements();
            return this.describeProdcessDoc;
        }else
            return null; 
    }
    
    this.addProcessingInfoElements=function (){
        var i;
        var processNode=this.describeProdcessDoc.getElementsByTagName("ProcessDescription")[0];
        
        var elInfo=processNode.getElementsByTagName("ows:Identifier")[0];
 
        if(!elInfo){
            elInfo=this.describeProdcessDoc.createElement ("ows:Identifier");
            processNode.appendChild (elInfo);
        }
            
        elInfo.textContent=this.Identifier;
        
        
        elInfo=processNode.getElementsByTagName("ows:Title")[0];
        if(!elInfo)
            elInfo=this.describeProdcessDoc.createElement ("ows:Title");
        elInfo.textContent=this.Title;
        processNode.appendChild (elInfo);
        
        elInfo=processNode.getElementsByTagName("ows:Abstract")[0];
        if(!elInfo)
            elInfo=this.describeProdcessDoc.createElement ("ows:Abstract");
        elInfo.textContent=this.Abstract;
        processNode.appendChild (elInfo);
        
        var metadata=processNode.getElementsByTagName("ows:Metadata");
        for(i=0; i<metadata.length;i++){
            processNode.removeChild(metadata[i]);
        }
        if(this.Metadata){
            if(this.Metadata.indexOf(",")!=-1){
                metadata=this.Metadata.split(","); 
                for(i=0; i<metadata.length;i++){
                    elInfo=this.describeProdcessDoc.createElement("ows:Metadata");
                    elInfo.setAttribute("xlink:title",metadata[i]);
                    processNode.appendChild (elInfo);
                }    
            }else
            if(this.Metadata!=""){
                elInfo=this.describeProdcessDoc.createElement("ows:Metadata");
                elInfo.setAttribute("xlink:title",this.Metadata);
                processNode.appendChild (elInfo);
            }
        }       
        var dataInputsNode=this.describeProdcessDoc.createElement ("DataInputs");
            processNode.appendChild(dataInputsNode);
            var processOutputsNode=this.describeProdcessDoc.createElement ("ProcessOutputs");
            processNode.appendChild(processOutputsNode);
    }


    this.addInputsElements=function(){
        for(var i=0; i<this.inputs.length; i++){
            this.inputs[i].addInputElement(this.describeProdcessDoc);  
            
        }
    }
    
    this.addOutputsElements=function(){
        for(var i=0; i<this.outputs.length; i++){
            this.outputs[i].addOutputElement(this.describeProdcessDoc); 
        }
    }

    this.getInputByIdentifier= function(identifier){
        for(var i=0; i<this.inputs.length; i++){
            if(this.inputs[i].Identifier == identifier)
                return this.inputs[i];
        }
        return null;
    };
    
    this.getOutputByIdentifier= function(identifier){
        for(var i=0; i<this.outputs.length; i++){
            if(this.outputs[i].Identifier == identifier)
                return this.outputs[i];
        }
        return null;
    };
    
    
    this.parseXMLDescribeProcess= function(describeProcessString){
        var describeDocument;
        if (window.DOMParser){
            var parser=new DOMParser();
            describeDocument=parser.parseFromString(describeProcessString,"text/xml");
        }
        else {
            describeDocument=new ActiveXObject("Microsoft.XMLDOM");
            describeDocument.async="false";
            describeDocument.loadXML(describeProcessString); 
        }

        var processDoc=describeDocument.getElementsByTagName("ProcessDescription")[0];
        var inputsElement=processDoc.getElementsByTagName("DataInputs")[0];
        var outputsElement=processDoc.getElementsByTagName("ProcessOutputs")[0];

        this.setProcessingInfoFromElement(processDoc);
        this.setInputsFromElement(inputsElement);
        this.setOutputsFromElement(outputsElement);
        
    };
    
    this.setProcessingInfoFromElement= function(processDocument){
      //  alert(processDocument);
      //  alert(processDocument.getElementsByTagName("ows:Identifier").length);
        var elInfo=processDocument.getElementsByTagName("Identifier")[0];
      //  alert(elInfo);
        if(elInfo)
            this.Identfier=elInfo.textContent; 
        
        elInfo=processDocument.getElementsByTagName("Title")[0];
        if(elInfo)
            this.Title=elInfo.textContent; 
       
        elInfo=processDocument.getElementsByTagName("Abstract")[0];
        if(elInfo)
            this.Abstract=elInfo.textContent; 
        
        var metadata=processDocument.getElementsByTagName("Metadata");
        
        if(metadata.length >0){
            this.Metadata="";
            for(var i=0; i< metadata.length; i++){
                this.Metadata+=metadata[i].textContent;
                if(i< metadata.length-1)
                    this.Metadata+=",";
          
            }
        } 
        
    }
    
    this.setInputsFromElement= function(inputsElement){
        
        var inputType, newInput;
        var inputsElementArray=inputsElement.getElementsByTagName("Input");
        for(var i=0; i<inputsElementArray.length; i++){
            inputType=this.getInputElementType(inputsElementArray[i]);
            if(inputType){
                newInput= new Input(inputType);
                newInput.setInputInformationByElement(inputsElementArray[i]);
                this.insertInput(newInput);
                
            } 
        }
    };
    
    this.setOutputsFromElement= function(outputsElement){
        var outputType, newOutput;
        var outputsElementArray=outputsElement.getElementsByTagName("Output");
        for(var i=0; i<outputsElementArray.length; i++){
            outputType=this.getInputElementType(outputsElementArray[i]);
            if(outputType){
                newOutput= new Input(outputType);
                newOutput.setInputInformationByElement(outputsElementArray[i]);
                this.insertOutput(newOutput);
            } 
        }
    };
    
    this.getInputElementType= function(inputElement){
        var resultArray=inputElement.getElementsByTagName("ComplexData");
        if(resultArray.length >0)
            return "ComplexData";
        else{
            resultArray=inputElement.getElementsByTagName("LiteralData");
            if(resultArray.length >0)
                return "LiteralData";
            else{
                resultArray=inputElement.getElementsByTagName("BoundingBoxData");
                if(resultArray.length >0)
                    return "BoundingBoxData";
                else
                    return null;   
            }
        }
    };
    
    this.getOutputElementType= function(outputElement){
        var resultArray=outputElement.getElementsByTagName("ComplexOutput");
        if(resultArray.length >0)
            return "ComplexOutput";
        else{
            resultArray=outputElement.getElementsByTagName("LiteralOutput");
            if(resultArray.length >0)
                return "LiteralOutput";
            else{
                resultArray=outputElement.getElementsByTagName("BoundingBoxOutput");
                if(resultArray.length >0)
                    return "BoundingBoxOutput";
                else
                    return null;   
            }
        }
    };
    
    this.init(describeProcessString);
};



LiteralDataInfo = function(){
    this.DataType = null;        // (short,int,double,float,String,boolean,long,byte)
    this.UOMsSupported = new Array();             // (meters, feet)
    this.DefaultValue = null ;    // String
    this.uomDefualtIndex= null;
    this.AllowedValues = new Array();   // (Byte,Int16,Int32,Float32....)   
    this.AllowedRanges = new Array();
    
    
    this.setDataType= function (dataType){
        this.DataType= dataType;
    }
    
    this.addUOM= function (uom){
        this.UOMsSupported.push(uom);
    }
    
    this.removeUOM= function (uom){
        var newUOMsSupported= new Array(); 
        for(var i=0; i<this.UOMsSupported.length; i++){
            if(this.UOMsSupported[i] != uom)
                newUOMsSupported.push(this.UOMsSupported[i]); 
        } 
        this.UOMsSupported=newUOMsSupported; 
    }; 
    
    this.removeUOMByIndex= function (uomIndex){
        var newUOMsSupported= new Array(); 
        for(var i=0; i<this.UOMsSupported.length; i++){
            if(i != uomIndex)
                newUOMsSupported.push(this.UOMsSupported[i]); 
        } 
        this.UOMsSupported=newUOMsSupported; 
    }; 
    
    this.setDefaultUOM= function (uomDefualt){
        for(var i=0; i<this.UOMsSupported.length; i++){
            if(this.UOMsSupported[i]==uomDefualt)
                this.uomDefualtIndex=i; 
        }
    
    }
    
    this.setDefaultValue= function (defualtValue){
        this.DefaultValue=defualtValue;
    }
    
    this.addAllowedValue= function(value){
        this.AllowedValues.push(value);  
    }
    
    this.removeAllowedValueByIndex= function (valueIndex){
        var newAllowedValues= new Array(); 
        for(var i=0; i<this.AllowedValues.length; i++){
            if(i != valueIndex)
                newAllowedValues.push(this.AllowedValues[i]); 
        } 
        this.addAllowedRange=newAllowedValues; 
    }; 
   
    this.addAllowedRange= function(minValue, maxValue){
        this.AllowedRanges.push({
            minValue: minValue,
            maxValue: maxValue
        });  
    }
  
    this.removeAllowedRangeByIndex= function (rangeIndex){
        var newAllowedRanges= new Array(); 
        for(var i=0; i<this.AllowedRanges.length; i++){
            if(i != rangeIndex)
                newAllowedRanges.push(this.AllowedRanges[i]); 
        } 
        this.AllowedRanges=newAllowedRanges; 
    };  
    
    
}


BoundingBoxDataInfo = function(){
    
    this.CRSSupported = new Array(); 
    
    this.crsDefualtIndex=null;
    
    this.addCRS= function (crsName){
        this.CRSSupported.push(crsName);   
    }
    
    this.setCRSList= function (crsList){
        var crsArray;
        if(crsList){
            if(crsList.indexOf(",")!=-1){
                crsArray=crsList.split(","); 
                for(var i=0; i<crsArray.length;i++){
                    this.addCRS(crsArray[i]);
                }    
                this.setDefault(crsArray[0]);
            }else
            if(crsList !=""){
                this.addCRS(crsList);
                this.setDefault(crsList);
            }
        } 
        
    };
    
    this.removeCRS= function (crsName){
        var newCRSArray= new Array(); 
        for(var i=0; i<this.CRSSupported.length; i++){
            if(this.CRSSupported[i] != crsName)
                newCRSArray.push(this.CRSSupported[i]); 
        } 
        this.CRSSupported=newCRSArray; 
    };

    this.removeCRSByIndex= function (crsIndex){
        var newCRSArray= new Array(); 
        for(var i=0; i<this.CRSSupported.length; i++){
            if(i != crsIndex)
                newCRSArray.push(this.CRSSupported[i]); 
        } 
        this.CRSSupported=newCRSArray; 
    };
    
    this.setDefault= function (crsDefualt){
        for(var i=0; i<this.CRSSupported.length; i++){
            if(this.CRSSupported[i]==crsDefualt)
                this.crsDefualtIndex=i; 
        }
    
    };
}


ComplexDataInfo = function(){
    this.formatSupported= new Array();
    
    this.formatDefualtIndex=null;
    
    this.MaximumMegabytes = null;
    
    this.setMaximumMegabytes = function (maxMegaBytes){
        this.MaximumMegabytes = maxMegaBytes;
    };
    
    this.addFormat= function(mimeType, encoding, schema){
        this.formatSupported.push({
            mimeType: mimeType,
            encoding: encoding,
            schema: schema
        });   
        
    };
    
    this.removeFormat= function (mimeType){
        var newFormatArray= new Array(); 
        for(var i=0; i<this.formatSupported.length; i++){
            if(this.formatSupported[i].mimeType != mimeType)
                newFormatArray.push(this.formatSupported[i]); 
        } 
        this.formatSupported=newFormatArray; 
    };
    
    this.removeFormatByIndex= function (formatIndex){
        var newFormatArray= new Array(); 
        for(var i=0; i<this.formatSupported.length; i++){
            if(i != formatIndex)
                newFormatArray.push(this.formatSupported[i]); 
        } 
        this.formatSupported=newFormatArray; 
    };
    
    this.setDefault= function (mimeTypeDefualt){
        for(var i=0; i<this.formatSupported.length; i++){
            if(this.formatSupported[i].mimeType == mimeTypeDefualt)
                this.formatDefualtIndex=i; 
        }
    };
    
}





