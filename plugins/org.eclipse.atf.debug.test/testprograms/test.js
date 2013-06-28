function evaled() {
	var code = "function fun() {        " +
			   "	a = 3 + 5;	        " + 
			   "	x = 'Number: ' + a; " +
			   "    a = 9; 		        " +
			   "} 				        ";
			
	eval(code);
}

function simple() {
	var aString = "Hello!";

	for(var i = 0; i < 10; i++) {
		aString = aString + i;
	}
	
	another();
}

function another() {
	var someVar = "Hello world.";
}