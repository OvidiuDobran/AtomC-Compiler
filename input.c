/*
testare analizor lexical
*/

//comm
void main()
{
	if(0xc==014)put_s("\"egal\"\t\t(h,o)"); // comm
		else put_s("\"inegal\"\t\t(h,o)");
		
//comm		
	if(20E-1==2.0&&0.2e+1==0x2)put_c('=');  // 2 scris in diverse feluri
		else put_c('\\');
}
