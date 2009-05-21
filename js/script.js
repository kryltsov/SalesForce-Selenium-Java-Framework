function getElementsByClass(searchClass,node,tag) {
    var classElements = new Array();
    if ( node == null )
        node = document;
    if ( tag == null )
        tag = '*';
    var els = node.getElementsByTagName(tag);
    var elsLen = els.length;
    var pattern = new RegExp('(^|\\\\s)'+searchClass+'(\\\\s|$)');
    for (var i = 0, j = 0; i < elsLen; i++) {
        if ( pattern.test(els[i].className) ) {
             classElements[j] = els[i];
             j++;
         }
    }
    return classElements;
}

function getEventsByStartId(startId){
	var nodes=document.getElementsByTagName('div');
	var nodesCount = nodes.length;
	var resultElements = new Array();
	var j=0;
	for (var k=0; k<nodesCount; k++){
		var temp = new Array();
		temp = nodes[k].id.split('_');
		if ( (temp[0]!='details') && (temp[0]==(startId)) ){
			resultElements[j]=nodes[k];
			j++;
		}
	}
	return resultElements;
} 

function getEventsByParentId(parentId){
	var nodes=document.getElementsByTagName('DIV');
	var nodesCount = nodes.length;
	var resultElements = new Array();
	var j=0;
	for (var i=0; i<nodesCount; i++){
		var temp = new Array();
		temp = nodes[i].id.split('_');
		if ((temp[0]!='details') && (temp[1]==parentId) ){
			//alert('good (parentId) = '+parentId);
			resultElements[j]=nodes[i];
			j++;
		}
	}
	return resultElements;
}
 
function getEventsByLogLevel(logLevel){
	var nodes=document.getElementsByTagName('div');
	var nodesCount = nodesLength;
	var resultElements = new Array();
	var j=0;
	for (var i=0; i<nodesCount; i++){
		var myStartId = getStartId(nodes[i].id);
		var myLogLevel = getLogLevel(nodes[i].id);
		if ( (myStartId!='details') && (myLogLevel==logLevel) ){
			resultElements[j]=nodes[i];
			j++;
		}
	}
	return resultElements;
}

function toggleNode(startId){
    var myImg=document.images[startId+'img'];
    var setToDisplayMode = 'none';
        
    if (myImg.src.indexOf('m.gif')>0){
            hideSubnodes(startId);
            myImg.src='../../img/p.gif';
    }
    else {
            showSubnodes(startId);
            myImg.src='../../img/m.gif';
    }    
}

function showSubnodes(parentId){
    var subnodes = getEventsByParentId(parentId);
    var subnodesCount = subnodes.length;

        for (var m=0; m<subnodesCount; m++){
            subnodes[m].style.display = 'block';
        }
}

function hideSubnodes(parentId){
//    var subnodes = getEventsByParentId(parentId);
    var subnodes = document.getElementsByName(parentId);
    var subnodesCount = subnodes.length;
    if (subnodesCount>0){
        for (var m=0; m<subnodesCount; m++){
            var myStartId = getStartId(subnodes[m].id);
            var myImg = document.images[myStartId+'img'];
            hideSubnodes( myStartId );
            subnodes[m].style.display = 'none';
            if (myImg!=null){
                myImg.src = '../../img/p.gif';
            }
        }
    }
}

function showParent(startId){
	var top=false;
	if (startId==0){
		startId=1;
		top=true;
	}
        var node = getEventsByStartId(startId)[0];
        if (node!=null){
	    var myImg=document.images[startId+'img'];
            node.style.display='block';
            myImg.src = '../../img/m.gif';
        }
    if (!top){
		    showParent(getParent(node.id));
    }
}

function getMyDisplayStyle(startId){
        if (startId=='0'){
            return 'block';
        }
	    var myImg=document.images[startId+'img'];
		var style='';

	    if (myImg.src.indexOf('p.gif')>0){
		style='none';
		}
	else {
		style='block';
	}
	return style;
}

function toggleLevel(chkBox){
    var els=getElementsByClass(chkBox.value,document,'div');
    var elsCount = els.length;
    
	if (chkBox.value == 'OK' && chkBox.checked){    
        getEventsByStartId('1')[0].style.display='block';    
        document.images['1img'].src = '../../img/p.gif';
	}
	
    if (chkBox.checked){
	for (var i=0; i<elsCount; i++){
            temp = new Array();
		temp = els[i].id.split('_');
		if (chkBox.value != 'OK'){

           els[i].style.display = 'block';
}
else{
            els[i].style.display = getMyDisplayStyle(temp[1]);
}
	        if (chkBox.value!='OK'){
		        showParent(temp[1]);
		}
        }
    }
	else {
        for (i=0; i<elsCount; i++){
            els[i].style.display = 'none';
        }
    }
}

function toggleDetails(textId){
var obj=document.getElementById(textId);
var img=document.images[textId+'img'];
if(obj.style.display=='none'){
	obj.style.display='block';
	img.src = '../../img/detailsHide.gif';
}else {
	obj.style.display='none';
	img.src = '../../img/detailsShow.gif';
}
}

function getParent (htmlId){
 temp = new Array();
 temp = htmlId.split('_');    
 return temp[1];
}

function getStartId (htmlId){
 temp = new Array();
 temp = htmlId.split('_');    
 return temp[0];
}

function getLogLevel (htmlId){
 temp = new Array();
 temp = htmlId.split('_');    
 return temp[2];
}

function onSelector(logLevel){
   	if (logLevel == '-1'){
        var nodes=document.getElementsByTagName('div');
        var nodesCount = nodes.length;
        for (i=0; i<nodesCount; i++){
            nodes[i].style.display = 'none';
            var img = document.images[getStartId( nodes[i].id)+'img'];
            if (img!=null){
                img.src = '../../img/p.gif';
            }
        }   
        getEventsByStartId('1')[0].style.display='block';    
        document.images['1img'].src = '../../img/p.gif';        
	}		

    var elsOk=getElementsByClass('OK',document,'div');
    var elsOkCount = elsOk.length;
    var elsError=getElementsByClass('ERROR',document,'div');
    var elsErrorCount = elsError.length;
    var elsFatal=getElementsByClass('FATAL',document,'div');
    var elsFatalCount = elsFatal.length;                
    
	if (logLevel == '0'){
            
        for (var i=0; i<elsOkCount; i++){
            elsOk[i].style.display = getMyDisplayStyle(getParent(elsOk[i].id));     
        }   
        for (var i=0; i<elsErrorCount; i++){
            elsError[i].style.display = getMyDisplayStyle(getParent(elsError[i].id));        
        }
        for (var i=0; i<elsFatalCount; i++){
            elsFatal[i].style.display = getMyDisplayStyle(getParent(elsFatal[i].id));                                
        }
        getEventsByStartId('1')[0].style.display='block';    
//        document.images['1img'].src = '../../img/p.gif';
	}
	
	if (logLevel == '1'){
            
        for (var i=0; i<elsOkCount; i++){
            elsOk[i].style.display = 'none';     
        }   

        for (var i=0; i<elsFatalCount; i++){
            elsFatal[i].style.display = 'block';                                
            var img = document.images[getStartId( elsFatal[i].id)+'img'];
            if (img!=null){
                img.src = img.src = '../../img/p.gif';
            }
            showParent(getParent(elsFatal[i].id));
        }
        for (var i=0; i<elsErrorCount; i++){
            elsError[i].style.display = 'block';        
            showParent(getParent(elsError[i].id));
            var img = document.images[getStartId( elsError[i].id)+'img'];
            if (img!=null){
                img.src = img.src = '../../img/p.gif';
            }
        }        
	}	

	if (logLevel == '2'){
            
        for (var i=0; i<elsOkCount; i++){
            elsOk[i].style.display = 'none';     
        }   
        for (var i=0; i<elsErrorCount; i++){
            elsError[i].style.display = 'none';        
        }
        for (var i=0; i<elsFatalCount; i++){
            elsFatal[i].style.display = 'block'; 
            var img = document.images[getStartId( elsFatal[i].id)+'img'];
            if (img!=null){
                img.src = img.src = '../../img/p.gif';
            }
            showParent(getParent(elsFatal[i].id));
        }
	}		
}