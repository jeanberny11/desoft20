
<?php include('functions.php'); 
if(!isset($_SESSION)){session_start();} 
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Facturacion Restaurant</title>

 <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1" />
  <link rel="stylesheet" href="css/bootstrap.css" type="text/css" />
  <link rel="stylesheet" href="css/font-awesome.min.css" type="text/css" />
  <link rel="stylesheet" href="css/font.css" type="text/css" cache="false" />
  <link rel="stylesheet" href="css/app.css" type="text/css" />
  <script src="js/jquery1.8.3.js"></script>
</head>

 <script language="javascript" type="text/javascript">
 
   function inicio1(){
    var nt = prompt("Dijite Cantidad de Pesos para el Incio Caja :");
	if (nt!=null){
	alert(nt)
 	  document.factura.paso50.value = nt;
	  document.factura.submit() 
     } }
   
   function recibo01(){
    document.factura.recibo1.value ='S';
    document.factura.submit() 
   }

    function cuadre1(){location.href="cuadre01.php?"; }
   
    function deli1(){location.href="enviod3.php?"; }
	
    function cambio(){
    document.factura.paso.value = 'S';
       }
    function cambioprecio(){
    document.factura.paso40.value = document.factura.paso.value + 'S';
   }
    function salvar01(){
    document.factura.paso10.value = document.factura.paso10.value + 'S';
    document.factura.submit() 
   }
    function salvar02(){
    document.factura.paso11.value = document.factura.paso11.value + 'S';
    document.factura.submit() 
   }
    function cancelar01(){
    document.factura.paso20.value = document.factura.paso20.value + 'S';
    document.factura.submit() 
   }
   function efectivo01(){
    var efp = document.factura.efectivo.value;
   	if (efp==""){
     document.factura.efectivo.value = 0.00; 
   }
   document.factura.efectivo.value = parseFloat(document.factura.efectivo.value) + 0.00;
    document.factura.submit() 
   }
  
function busqueda2(){
valor = document.getElementById("busqueda1").value;
if( valor !="BUSQUEDA" ) {
  document.factura.submit() 
} }
  
function busqueda21(){
valor = document.getElementById("orden1").value;
if( valor !="ORDENES" ) {
  document.factura.submit() 
} }

  function busqueda22(){
valor = document.getElementById("mesas").value;
if( valor !="BUSQUEDA" ) {
  document.factura.submit() 
} }
  
  
  
 
  
  function reimprimir1(){
    var nt = prompt("Dijite No.Factura :");
	if (nt!=null){
	alert(nt)
 	location.href="reimpresion.php?valor="+nt; 
     } }
 
   function anulart1(){
        var nt = prompt("Clave de Autorizacion");
	if (nt!=null){
		location.href="factura.php?clavepp="+nt; 
   } }
   
    function anular01(){
    var nt1 = prompt("Dijite No.Factura Anular:");
	if (nt1!=null){
	alert(nt1)
		location.href="anularfact.php?valor="+nt1;  
   } }
   
   
    function cancelar02(){
    document.factura.paso30.value = document.factura.paso30.value + 'S';
    document.factura.submit() 
   }
  
    function salir1(){location.href="menu.php?";  }

 </script>
<?php
 
  include('controlhora.php');
 $central =  $_SESSION['central'];
 $estafeta = $_SESSION['estafeta'];
 $nfactura = $_SESSION['nfactura'];
 $fecha = $_SESSION['fecha'];
 $hora = $_SESSION['horap'];
 

 if(isset($_POST['r01'])) {
header('Location: recibird2.php');
}
if(isset($_POST['bar01'])) {
header('Location: pedido2.php');
}

?>




<body>
 <header>
 
 <div id="pantalla00" class="container"> 
 
<form class="form horizontal" id="factura" name="factura" method="post" action="factura.php"  >
 <div class="table-responsive">
 <table class="table table-striped table-bordered table-hover table-condensed " >
   <tr class="info">
    <th colspan="4">
	
<?php 
$centralg =$_SESSION['central'];$estafeta =$_SESSION['estafeta'];
$inicio11 = mysqli_query($link, "select * from master where  codmaster='$estafeta' AND centralmaster='$centralg' ");
$inicio111 = mysqli_num_rows($inicio11); if ($inicio111 > 0 ){ $inicio21 = mysqli_fetch_array($inicio11); $factp=$inicio21['factura']; 
$iley=trim($inicio21['iley']); $ley=$inicio21['ley'];
$tncf=$inicio21['tncf'];$it=$inicio21['itbi'];} if(trim($tncf)=="No Aplica" or trim($tncf)=="Regimes Especiales" ){$it="0";}
$_SESSION['nfactura']=$factp; $_SESSION['listado5']=$it;  $_SESSION['listado4']=$ley;if(trim($tncf)==""){$tncf="No Aplica";} $_SESSION['listado3']=$tncf; $_SESSION['listado6']=$iley;?>
 No.Fact: <b>
 <input class="info" style="font-size:18px"  name="nfactura" type="text"  id ="nfactura" value="<?php echo $_SESSION['nfactura']; ?>" size="5" READONLY/>
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;	  
	  <b><FONT SIZE=4>F A C T U R A C I O N</FONT>
     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <b><FONT SIZE=3><?php echo $_SESSION['fecha'].'&nbsp;&nbsp;&nbsp;&nbsp'.$_SESSION['hora']; ?> </FONT> 
         &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

				
<?php   
$fotop= $_SESSION['fondo1'];  if(trim($fotop)!=""){  ?>
 <img border border="2" src="<?php echo $_SESSION['fondo1']; ?>" width="100"  height="40" title="Soluciones Tecnologica LOTECOM.NET" />
  <?php }   ?> 
   </th>
   </tr> 
  
   
  <tr class="info">
   <th>
   <select name="ncf" size="1" id="ncf">
  <option value="<?php echo $_SESSION['listado3']; ?>"><?php echo $_SESSION['listado3']; ?></option>
  <option value="No Aplica">No Aplica</option>
  <option value="Sin Valor Credito Fiscal">Sin Valor Credito Fiscal</option>
  <option value="Con Valor Credito Fiscal">Con Valor Credito Fiscal</option>
  <option value="Gubernamental">Gubernamental</option>
  <option value="Regimes Especiales">Regimes Especiales</option>
  <option value="Sin Cabeza">Sin Cabeza</option>
   </select>
   <input style="font-size:12px"  size="20" type="text" name="ncf1" placeholder="N C F"  value="" id="ncf1"  READONLY >
  </th>
  
  <th> <input style="font-size:12px"  size="20" type="text" name="bnombre" placeholder="Buscar"  value="" id="bnombre"   >
	 
 <input class="info" style="font-size:12px"  name="ordeng" type="text"  id ="ordeng" value="" size="5" >
 <select name="orden1" size="1" value ="BUSQUEDA" id="orden1" onclick="busqueda21()" > 
  <option value="ORDENES">ORDENES</option>
 <?php 
 $centralg =$_SESSION['central'];
 $estafeta=  $_SESSION['estafeta'];
$buscar1 = mysqli_query($link, "select * from  cotizacion where  posteo='Normal' AND estafeta='$estafeta' AND central='$centralg' ORDER BY cotizacion ASC  ");
while ($row = mysqli_fetch_assoc($buscar1)){
 ?> <option value="<?php  echo $row['cotizacion'].'='.substr($row['nombre'],0,15); ?>"><?php  echo  $row['cotizacion'].'='.substr($row['nombre'],0,15); ?></option> <?php 
}?>
 </select>
	 
	 
  <select name="busqueda1" size="1" value ="BUSQUEDA" id="busqueda1" onclick="busqueda2()" > 
  <option value="BUSQUEDA">BUSQUEDA</option>
 <?php 
 $centralg =$_SESSION['central'];
 $estafeta=  $_SESSION['estafeta'];
$buscar1 = mysqli_query($link, "select * from  cliente where  estafeta='$estafeta' AND central='$centralg' ORDER BY codigo ASC  ");
while ($row = mysqli_fetch_assoc($buscar1)){
 ?> <option value="<?php  echo $row['codigo'].'='.substr($row['nombre'],0,18); ?>"><?php  echo  $row['codigo'].'='.substr($row['nombre'],0,18); ?></option> <?php 
}?>
 </select>
 
 
 
   <select name="condicion" size="1" id="condicion">
  <option value="CONTADO">CONTADO</option>
  <option value="CREDITO">CREDITO</option>
   </select>
</th>
<th colspan="2">

 Printer:
   <select name="printerp" size="1" id="printerp">
  <option value="<?php echo $_SESSION['printer']; ?>"><?php echo $_SESSION['printer']; ?></option>
  <option value="P">P</option>
  <option value="D">D</option>
  <option value="N">N</option>
  <option value="G">G</option>
   </select>
   Itbis Incl:
   <select name="itbiincluido" size="1" id="itbiincluido">
  <option value="<?php echo $_SESSION['itbiincluido']; ?>"><?php echo $_SESSION['itbiincluido']; ?></option>
  <option value="S">S</option>
  <option value="N">N</option>
  </select>
  
  
   <select name="vendedor" size="1" value ="" id="vendedor"  > 
       <option value="VENDEDOR">VENDEDOR</option> 
     <?php 
$centralg =  $_SESSION['central'];
$estafeta =  $_SESSION['estafeta'];
$buscar1 = mysqli_query($link, "select * from  vendedor where estafeta='$estafeta' and central='$centralg' ORDER BY codigo ASC ");
while ($row = mysqli_fetch_assoc($buscar1)){
 ?> <option value="<?php  echo $row['codigo'].'='.substr($row['nombre'],0,8); ?>"><?php  echo  $row['codigo'].'='.substr($row['nombre'],0,8); ?></option> <?php 
}

?>
 </select>
   
    
   
   </th>
  </tr> 
  
  
  
 <tr class="info">
  <th colspan="4">
Cte:
 <input class="info" style="font-size:12px"  name="cliente" type="text" placeholder="Cliente"  id ="cliente" value="" size="10" >
 Reg.Orden:
 <input style="font-size:12px"  size="4" type="text" name="orden" value="" id="orden" > 
   <input style="font-size:12px" name="nombre" placeholder="Nombre"   type="text" size="25" id = "nombre"  />
  <input style="font-size:12px" name="direccion" placeholder="Direccion"  type="text" size="25" id = "direccion"  /> 
  <input style="font-size:12px" name="telefono" type="text" placeholder="Telefonos" size="15" id = "telefono"  /> 
  <input style="font-size:12px" name="rnc" type="text" placeholder="RNC"  size="10" id = "rnc"  />
 
  </th> 
  </tr>  
	
    <tr class="info">
      <th colspan="4">  
  <input class="info" style="font-size:12px"  name="articulo" type="text" placeholder="Articulo"  id ="articulo" value="" size="15" autofocus />
    
  <input class="info" style="font-size:12px"  name="descripcion" type="text" placeholder="Descripcion"  id ="descripcion" value="" size="30" />
     <input class="info" style="font-size:12px"  name="comentario" type="text" placeholder="Comentario"  id ="comentario" value="" size="30" />
  
  <select name="unidad" size="1" id="unidad"   onfocus="cambioprecio();"  >
     <option value="UNIDAD">UNIDAD</option>
   <?php 
    
 $centralg =$_SESSION['central'];
 $estafeta=  $_SESSION['estafeta'];
  if(isset($_POST['articulo'])) {$articulo =$_POST['articulo'];$articulo =strtoupper(trim($articulo));$_SESSION['lista1'] =$articulo;} else {$articulo ="";}
  
$inicio = mysqli_query($link, "select * from articulos where codigo='$articulo' AND estafeta='$estafeta' AND central='$centralg' ");
	$inicio1 = mysqli_num_rows($inicio);
	if ($inicio1 > 0 ){ $inicio2 = mysqli_fetch_array($inicio);	
	?> <option value="<?php  echo $inicio2['unidad']; ?>"><?php  echo  $inicio2['unidad']; ?></option> <?php 
     if (trim($inicio2['precio3'])!=""){
	?> <option value="<?php  echo $inicio2['unidad2']; ?>"><?php  echo  $inicio2['unidad2']; ?></option> <?php 
     }
   if (trim($inicio2['precio5'])!=""){
	?> <option value="<?php  echo $inicio2['unidad3']; ?>"><?php  echo  $inicio2['unidad3']; ?></option> <?php 
     }
   if (trim($inicio2['precio7'])!=""){
	?> <option value="<?php  echo $inicio2['unidad4']; ?>"><?php  echo  $inicio2['unidad4']; ?></option> <?php 
     } }
 
  ?>
   </select>
   <input class="info" style="font-size:12px"  name="cantidad" type="text" placeholder="Cantidad"  id ="cantidad" value="" size="6"  
    onkeyup="cambio();"  />
	 
  <input class="info" style="font-size:12px"  name="precio" type="text" placeholder="Precio"  id ="precio" value="" size="6"  />
    
  <input class="info" style="font-size:12px"  name="desci" type="text" placeholder="Desc."  id ="desci" value="" size="1" />
  
    <input name="grupo" type="hidden"  size="10" id = "grupo" />
   <input name="tpedido" type="hidden"  size="10" id = "tpedido" />
     <input name="foto" type="hidden"  size="100" id = "foto" />
    <input  size="10"  type="hidden" name="recibo1"   value="" id="recibo1"   READONLY >
  </th>
   </tr> 
</table> 

<!-- Inicio de la Pantalla 01 o Pagao Factura-->
 <div  id="pantalla01" class="info" style="visibility:hidden;position:absolute; border:3px solid #467BD7;width:300px;height:375px; margin-left:250px; margin-right:150px;background-color:#E0FFFF">
   </br>
     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	 <b><FONT SIZE=4>FORMA DE PAGO</FONT>
	</br></br>
  &nbsp;&nbsp;Importe:
<input class="info" style="font-size:28px" type="text" name="importe2" size="6" value=""  id = "importe2" placeholder="Importe"  READONLY /></br>
 &nbsp;&nbsp;&nbsp;&nbsp;Tarjeta:
<input class="info" style="font-size:28px"   type="text" name="tarjeta" size="6" value="" id="tarjeta" placeholder="tarjeta"  /></br>
  &nbsp;&nbsp;&nbsp;Cheque:
<input class="info" style="font-size:28px"  type="text" name="cheque" size="6" value="" id="cheque" placeholder="Cheque"  /></br>
  &nbsp;&nbsp;Efectivo:
<input class="info" style="font-size:28px"  type="text" name="efectivo" size="6" value="" id="efectivo" placeholder="Efectivo"  onkeypress = "if(event.keyCode == 13) efectivo01()" autofocus /></br>
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp
<input class="info" style="font-size:35px"  type="text" name="devuelta" size="7" value="" id ="devuelta" placeholder="Devuelta" READONLY /></br>
</br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 <button class="btn btn-sm btn-primary" id="salvarp" name="salvarp" value="" type="button" onclick="salvar01();">Salvar</button>
&nbsp;&nbsp;&nbsp;&nbsp;
<button class="btn btn-sm btn-primary" id="cancelarp" name="cancelarp" value="cancelarp" type="button" onclick="cancelar01();" >Cancelar</button>
</div>
<!-- Fin Pantalla 01 -->

<!-- Inicio de Recibo de Ingreso -->
 <div  id="pantalla03" class="info" style="visibility:hidden;position:absolute; border:3px solid #467BD7;width:550px;height:250px; margin-left:250px; margin-right:150px;background-color:#E0FFFF">
   </br>
     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	 <b><FONT SIZE=4>R E C I B O &nbsp;&nbsp;&nbsp;D E &nbsp;&nbsp;&nbsp;I N G R E S O</FONT>
	</br></br>
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input class="info" style="font-size:18px" type="text" name="nomrec" size="40" value=""  id = "nomrec" placeholder="Nombre" /></br>
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input class="info" style="font-size:18px"   type="text" name="direc" size="40" value="" id="direc" placeholder="Direccion"  /></br>
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input class="info" style="font-size:18px"  type="text" name="telrec" size="40" value="" id="telrec" placeholder="Telefono"  /></br>
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input class="info" style="font-size:18px"  type="text" name="montorec" size="12" value="" id="montorec" placeholder="Monto"  /></br>

 </br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 <button class="btn btn-sm btn-primary" id="salrec" name="salrec" value="" type="button" onclick="salvar02();">Salvar</button>
&nbsp;&nbsp;&nbsp;&nbsp;
<button class="btn btn-sm btn-primary" id="cancelare" name="cancelare" value="cancelarp" type="button" onclick="cancelar01();" >Cancelar</button>
</div>
<!-- Fin Envio -->





</div>
</div>
	 <!-- Inicio de la Pantalla 01 o Pagao Factura-->
 <div  id="pantalla12" class="info" style="visibility:hidden;position:absolute; border:3px solid #467BD7;width:100%;height:100%; background-color:#E0FFFF">
   </br>
     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	 <b><FONT SIZE=4>FORMA DE PAGO</FONT>
	</br></br>
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

</div>
<!-- Fin Pantalla 01 -->
</header>
 <?php 
$existe="N";
$centralg =$_SESSION['central'];
$estafeta =$_SESSION['estafeta'];
$fecha =$_SESSION['fecha'];
$usuariop =$_SESSION['usuario'];
$costo =0.00;$secuencia =1;$suplidor ="";$zona ="";$grupo ="";
$secu =$_SESSION['hora']; $tncf=$_SESSION['listado3']; 
if(trim($centralg)==""){echo '<script language="javascript">alert("Atencion... Esta Fuera de  SECCION. Debe Reinicial el Sistema........");</script>';header('Location: http://168.144.159.40/prestamos/login.php');}
//Buscar Codigo para Actualizar
//Borrar Codigo
 

if(isset($_POST['printerp'])) {$printer =$_POST['printerp'];$printer =strtoupper(trim($printer));} else {$printer = $_SESSION['printer']; }
if(isset($_POST['ncf'])) {$ncf= $_POST['ncf'];} else {$ncf= $tncf;}
if(isset($_POST['nfactura'])) {$factura= $_POST['nfactura'];} else {$factura= $_SESSION['nfactura'];}
if(isset($_POST['rnc'])) {$rnc= $_POST['rnc'];}  else {$rnc= "";}
if(isset($_POST['ncf1'])) {$ncf1= $_POST['ncf1'];} else {$ncf1= $tncf;}
if(isset($_GET['delip'])) {$delip= $_GET['delip'];} else {$delip= "";}
if(isset($_POST['vendedor'])) {$valor1 = $_POST['vendedor'];$vendedorp2=$_POST['vendedor'];$valor2=split("[=]",$valor1);$vendedor=$valor2[0]; } else {$vendedor ="1";$vendedorp2='VENDEDOR';}
if(isset($_POST['orden'])) {$orden =$_POST['orden'];$orden =strtoupper(trim($orden));}  else {$orden ="";}
if(isset($_POST['ordeng'])) {$ordeng =$_POST['ordeng'];$ordeng =strtoupper(trim($ordeng));}  else {$ordeng ="";}
if(isset($_POST['condicion'])){$condicion=$_POST['condicion'];$condicion=strtoupper(trim($condicion));}else{$condicion="CONTADO";}
if(isset($_POST['cliente'])) {$cliente =$_POST['cliente'];$cliente =strtoupper(trim($cliente));} else {$cliente ="1";$existe="S";}
if(isset($_POST['clientesep'])) {$clientesep =$_POST['clientesep'];$clientesep =strtoupper(trim($clientesep));} else {$clientesep ="";}
if(isset($_POST['bnombre'])) {$bnombre =$_POST['bnombre'];$bnombre =strtoupper(trim($bnombre));} else {$bnombre ="";}
if(isset($_POST['bnombre2'])) {$bnombre2 =$_POST['bnombre2'];$bnombre2 =strtoupper(trim($bnombre2));} else {$bnombre2 ="";}
if(isset($_POST['nombre'])) {$nombre =$_POST['nombre'];$nombre =strtoupper(trim($nombre));} else {$nombre ="";}
if(isset($_POST['direccion'])) {$direccion =$_POST['direccion'];$direccion =strtoupper(trim($direccion));} else {$direccion ="";}
if(isset($_POST['telefono'])) {$telefono =$_POST['telefono'];$telefono =strtoupper(trim($telefono));} else {$telefono ="";}
if(isset($_POST['balance'])) {$balance =$_POST['balance'];$balance =strtoupper(trim($balance));} else {$balance ="";}
if(isset($_POST['articulo'])) {$articulo =$_POST['articulo'];$articulo =strtoupper(trim($articulo));$_SESSION['lista1'] =$articulo;} else {$articulo ="";}
if(isset($_POST['descripcion'])){$descripcion=$_POST['descripcion'];$descripcion=strtoupper(trim($descripcion));}else{$descripcion ="";}
if(isset($_POST['unidad'])) {$unidad =$_POST['unidad'];$unidad =strtoupper(trim($unidad));} else {$unidad =$_POST['unidad'];}
if(isset($_POST['cantidad'])) {$cantidad =$_POST['cantidad'];$cantidad =strtoupper(trim($cantidad));} else {$cantidad ="";}
if(isset($_POST['precio'])) {$precio =$_POST['precio'];$precio =strtoupper(trim($precio));} else {$precio ="";}
if(isset($_POST['comentario'])){$comentario=$_POST['comentario'];$comentario=strtoupper(trim($comentario));} else{$comentario="";}
if(isset($_POST['desci'])) {$desci =$_POST['desci'];} else {$desci ="";}
if(isset($_POST['descp'])) {$descp =$_POST['descp'];} else {$descp ="";}
if(isset($_POST['descd'])) {$descd =$_POST['descd'];} else {$descd ="";}
if(isset($_POST['paso'])) {$paso =$_POST['paso'];} 
if(isset($_POST['tpedido'])) {$tpedido =$_POST['tpedido'];} else {$tpedido="N";} 
if(isset($_POST['neto'])) {$neto =$_POST['neto'];} else {$neto =9;} 
if(isset($_POST['paso10'])) {$paso10 =$_POST['paso10'];} else {$paso10="";}
if(isset($_POST['grupo'])) {$grupo =$_POST['grupo'];} else {$grupo="";}
if(isset($_POST['foto'])) {$foto =$_POST['foto'];} else {$foto="";}
if(isset($_POST['paso11'])) {$paso11 =$_POST['paso11'];} else {$paso11="";}
if(isset($_POST['envio1'])) {$envio1 =$_POST['envio1'];} else {$envio1="";}
if(isset($_POST['paso20'])) {$paso20 =$_POST['paso20'];} else {$paso20="";}
if(isset($_POST['paso30'])) {$paso30 =$_POST['paso30'];} else {$paso30="";}
if(isset($_POST['paso40'])) {$paso40 =$_POST['paso40'];} else {$paso40="";}
if(isset($_POST['paso50'])) {$paso50 =$_POST['paso50'];} else {$paso50="";}
if(isset($_GET['clavepp'])) {$clavepp =$_GET['clavepp'];$clavepp =strtoupper(trim($clavepp));} else {$clavepp="";}
if(isset($_GET['clavepp2'])) {$clavepp2 =$_GET['clavepp2'];$clavepp2 =strtoupper(trim($clavepp2));} else {$clavepp2="";}
if(isset($_GET['clavepp3'])) {$clavepp3 =$_GET['clavepp3'];$clavepp3 =strtoupper(trim($clavepp3));} else {$clavepp3="";}
if(isset($_POST['impuesto'])) {$impuesto =$_POST['impuesto'];} else {$impuesto ="S";}
if(isset($_POST['impuestoley'])) {$impuestoley =$_POST['impuestoley'];} else {$impuestoley ="S";}
if(isset($_POST['preciom'])) {$preciom =$_POST['preciom'];} else {$preciom ="0";}
if(isset($_POST['existencia'])) {$existencia =$_POST['existencia'];} else {$existencia ="";}
if(isset($_GET['visualizar'])) {$valor1 = $_GET['visualizar'];
$valor2=split("[=]",$valor1);$codp=$valor2[0];$orden=$valor2[1];$ncf=$valor2[2];$ncf1=$valor2[3];$condicion=$valor2[4];$vendedor=$valor2[5]; $vendedorp2=$valor2[5].'='.$valor2[6];$camarero=$valor2[7];$clientesep=$valor2[8];$cliente =$codp;$nombre ="";} 
if(isset($_GET['visualizar2'])) {
$valor1 = $_GET['visualizar2'];
$valor2=split("[=]",$valor1);$codp=$valor2[0];$cliente=$valor2[1];$orden=$valor2[2];$ncf=$valor2[3];$ncf1=$valor2[4];$condicion=$valor2[5];$vendedor=$valor2[6]; $vendedorp2=$valor2[6].'='.$valor2[7];$camarero=$valor2[8];$clientesep=$valor2[9];$articulo=$codp;$bnombre2="";}
if(isset($_POST['efectivo'])) {$efectivo =$_POST['efectivo'];} else {$efectivo ="";}
if(isset($_POST['tgeneral'])) {$tgeneral =$_POST['tgeneral'];} else {$tgeneral ="";}
if(isset($_POST['tdescuento'])) {$tdescuento =$_POST['tdescuento'];} else {$tdescuento ="";}
if(isset($_POST['titbi'])) {$titbi =$_POST['titbi'];} else {$titbi ="";}
if(isset($_POST['devuelta'])) {$devuelta =$_POST['devuelta'];} else {$devuelta ="";}
if(isset($_POST['tarjeta'])) {$tarjeta =$_POST['tarjeta'];$tarjeta = bcadd($tarjeta,'0',2);} else {$tarjeta ="";}
if(isset($_POST['cheque'])) {$cheque =$_POST['cheque'];$cheque = bcadd($cheque,'0',2);} else {$cheque ="";}
if( trim($paso20)!="" ){$articulo="";$efectivo="";$cheque=""; $tarjeta="";$paso20="";}
if(isset($_POST['nomenvio'])) {$nomenvio =$_POST['nomenvio'];$nomenvio =strtoupper(trim($nomenvio));} else {$nomenvio ="";}
if(isset($_POST['direnvio'])) {$direnvio =$_POST['direnvio'];$direnvio =strtoupper(trim($direnvio));} else {$direnvio ="";}
if(isset($_POST['telenvio'])) {$telenvio =$_POST['telenvio'];$telenvio =strtoupper(trim($telenvio));} else {$telenvio ="";}
if(isset($_POST['itbiincluido'])) {$itbiincluido =$_POST['itbiincluido'];} else {$itbiincluido= $_SESSION['itbiincluido'];}
if(isset($_POST['ley'])) {$ley =$_POST['ley'];} else {$ley= $_SESSION['listado4'];}
if(isset($_POST['pley'])) {$pley =$_POST['pley'];} else {$pley= "";}
if(isset($_POST['tp20'])) {$tp20 =$_POST['tp20'];} else {$tp20= "";}
if(isset($_POST['recibo1'])) {$recibo1 =$_POST['recibo1'];} else {$recibo1= "";}
if(isset($_POST['mesas'])) {$valor1 =$_POST['mesas'];
if(isset($_POST['nomrec'])) {$nomrec =$_POST['nomrec'];$nomrec =strtoupper(trim($nomrec));} else {$nomrec ="12345";}
if(isset($_POST['direc'])) {$direc =$_POST['direc'];$direc =strtoupper(trim($direc));} else {$direc ="";}
if(isset($_POST['telrec'])) {$telrec =$_POST['telrec'];$telrec =strtoupper(trim($telrec));} else {$telrec ="";}
if(isset($_POST['montorec'])) {$montorec =$_POST['montorec'];$montorec =strtoupper(trim($montorec));} else {$montorec ="0";}


$valor2=split("[=]",$valor1);if(trim($valor1)!="BUSQUEDA"){$orden=$valor2[0];$clientesep=$valor2[1]; $nombre=$valor2[1]; }}

if(isset($_POST['orden1'])) {$valor1 = $_POST['orden1'];$valor2=split("[=]",$valor1);$valor3=$valor2[0];
if($valor3!="ORDENES"){$ordeng=$valor2[0];$ordeng =strtoupper(trim($ordeng));}}


if(isset($_GET['borrar'])){ 
$valor1 = $_GET['borrar'];
$valor2=split("[=]",$valor1);
$cod=$valor2[0];$cliente=$valor2[1];$orden=$valor2[2];$ncf=$valor2[3];$ncf1=$valor2[4];$condicion=$valor2[5];$vendedor=$valor2[6]; $vendedorp2=$valor2[6].'='.$valor2[7];$camarero=$valor2[8]; $clientesep=$valor2[9];
$borrar =  mysqli_query($link, " DELETE FROM temporal WHERE  secuencia='$cod' and estafeta ='$estafeta' and  central='$centralg' ");
$borrar2 =  mysqli_query($link, " DELETE FROM  cotizaciondet WHERE secuencia='$cod' and estafeta ='$estafeta' and  central='$centralg'");
 $articulo="";  }
//deli


if(trim($delip)!="" and $tgeneral >1 ){ $paso="N";
$buscar1 = mysqli_query($link, "select * from  vendedor where codigo='$delip' and estafeta='$estafeta' and central='$centralg' ORDER BY codigo ASC "); while ($row = mysqli_fetch_assoc($buscar1)){$paso="S";}
if(trim($paso)=="N"){echo '<script language="javascript">alert("Atencion... Codigo de NO Existe o Importe en Blanco.");</script>';  }

}
 
 // Recibo de Ingreso 
  ?>   <script language="javascript" type="text/javascript">
   document.getElementById('nomrec').value= "<?php echo $nomrec; ?>";
   document.getElementById('direc').value= "<?php echo $direc; ?>";
   document.getElementById('telrec').value= "<?php echo $telrec; ?>";
   document.getElementById('montorec').value= "<?php echo $montorec; ?>"; 
     </script> <?php 
 
   if(trim($recibo1)!=""  and (int)$neto > 0 ){
   $nomrec=$nombre;$direc=$direccion;$telrec=$telefono;   ?> 
   <script language="javascript" type="text/javascript">
     document.all.item("pantalla03").style.visibility='visible' 
    </script> <?php 
   if($nomrec==""){?> <script language="JavaScript">
   document.getElementById('nomrec').focus(); <?php } ?>  </script>  <?php  
   if( $nomrec!="" and $direc==""){?> <script language="JavaScript">
   document.getElementById('direc').focus(); <?php } ?>  </script>  <?php  
   if( $nomrec!="" and $direc!="" and $telrec==""  ){?> <script language="JavaScript">
   document.getElementById('telrec').focus(); <?php } ?>  </script>  <?php  
 if( $nomrec!="" and $direc!="" and $telrec!=""  and $montorec==""  ){?> <script language="JavaScript">
   document.getElementById('montorec').focus(); <?php } ?>  </script>  <?php  
	}


if($montorec>5){ if($cliente=="1"){

echo "kkkkkk $montorec    $cliente";
$inicio = mysqli_query($link, "select * from master where  codmaster='$estafeta' AND centralmaster='$centralg' ");
$inicio1 = mysqli_num_rows($inicio); if ($inicio1 > 0 ){ $inicio2 = mysqli_fetch_array($inicio); $cliente= $inicio2['ccliente']; }
$clip3=$cliente+1;$nombre=$nomrec; $direccion=$direc; $telefono=$telrec;
$pp111 = mysqli_query($link, "UPDATE master set ccliente='$clip3' where centralmaster='$centralg' and  codmaster='$estafeta' ");
	$pp3 = mysqli_query($link, "INSERT INTO cliente(central,estafeta,codigo,nombre,direccion,telefono,balance) values ('$centralg','$estafeta','$cliente','$nombre','$direccion','$telefono','0.00' )");}$condicion="CREDITO";$articulo="*";
 }

 //fin recibo de ingreso
 
  
if(trim($condicion)=="CREDITO" and trim($cliente)=="1"){ echo '<script language="javascript">alert("Atencion... Verifique El CODIGO del Cliente es Codigo Factura Contado...");</script>';$condicion="CONTADO";}

 
 //inicio Caja
   if(trim($paso50)!="") {
   //Validar el monto solo numero
  $str = strlen($paso50);  $x = 0;$p1t="S";$p=$paso50;
  while ($x < $str){$k1 = substr($p, $x, 1);if($k1!="0" and  $k1!="1"  and  $k1!="2" and  $k1!="3" and  $k1!="4" and  $k1!="5"  and  $k1!="6"and  $k1!="7" and  $k1!="8"  and  $k1!="9"and $k1!="."){$p1t="N";$x=$str;$paso50="";} $x++;}
 //fin de la validacion
 if(trim($paso50)==""){echo '<script language="javascript">alert("Atencion... La Caja No se Inico Error en la Dijitacion.");</script>'; }
  $codpp=$_SESSION['nombre']; $inicio11 = mysqli_query($link, "select * from  login where  username='$codpp' AND central='$centralg'  ");
  $inicio12 = mysqli_num_rows($inicio11);
  if ($inicio12 > 0 ){$inicio22 = mysqli_fetch_array($inicio11);	
	 $cajap = $inicio22['caja'];
  	 if($cajap > 2){echo '<script language="javascript">alert("Atencion... La Caja No se Inico Error Debe Cerrar el Cuadre anterior.");     </script>';  } else { $pp11 = mysqli_query($link, "UPDATE login set caja='$paso50' where central='$centralg' and username='$codpp' "     );} }   }
 //fin inicio Caja
 


//Transferir Ordenes o Cotizacion 
 if( trim($ordeng)!="" ) {
 $buscar1 = mysqli_query($link,"select * from cotizacion where posteo!='Procesado' and cotizacion ='$ordeng' and estafeta ='$estafeta' and  central='$centralg'");
while ($row = mysqli_fetch_assoc($buscar1)){
if (trim($ordeng)!=trim($tp20)){
$cliente=$row['cliente']; $nombre = $row['nombre'];$direccion = $row['direccion']; $telefono = $row['telefono']; }}

$buscar1 = mysqli_query($link, "select * from cotizaciondet where paso!='Reg' and posteo!='Procesado' and cotizacion ='$ordeng' and estafeta ='$estafeta' and  central='$centralg' ");
while ($row = mysqli_fetch_assoc($buscar1)){
$secuencia=$row['secuencia']; $articulo2 = $row['articulo'];$descripcion2 = $row['descripcion'];
 $comentario2 = $row['mecanico'];
  if (trim($comentario2)!=""){$comentario2 = 'At.Por='.$row['mecanico']; } else {$comentario2 = $row['comentario'];}
$precio2 = $row['precio']; $cantidad2 = $row['cantidad'];
 $pp12 = mysqli_query($link, "UPDATE cotizaciondet set paso='Reg' where  secuencia='$secuencia' and estafeta ='$estafeta' and  central='$centralg'  ");
    
 $inicio = mysqli_query($link, "select * from temporal where articulo='$articulo2' AND usuario='$usuariop' AND estafeta='$estafeta' AND central='$centralg' ");
$inicio1 = mysqli_num_rows($inicio);
if ($inicio1 > 0 ){
$inicio221 = mysqli_fetch_array($inicio);	
$canpp= $inicio221['cantidad'];	$prepp= $inicio221['precio'];	
$balp1=$canpp*$prepp;  $balp2=$cantidad2*$precio2;  $cantidad2=$canpp+$cantidad2; $precio2= ($balp1+$balp2)/$cantidad2;
$tg = $precio2 * $cantidad2;
if(trim($descp)=="" or trim($descd)==""){$tdescuento2=$tg * ((int)$desci/100);} else {$tdescuento2="";}
if ((int)$descp > 0 ){$tdescuento2=$tg * ($descp/100);}
$importe2= $tg-$tdescuento2;
if(trim($impuesto)!="N"){
if(trim($itbiincluido)!="N"){
$titbi2= $importe2-($importe2 / (1+($pitbi/100)));} else {$titbi2= $importe2*($pitbi/100); $importe2=$importe2+$titbi2; $tg=$tg+$titbi2;}
} else {$titbi2="";}
$tgeneral2=$tg-$titbi2;
$pp11 = mysqli_query($link, "UPDATE temporal set cantidad='$cantidad2',unidad='$unidad',precio='$precio2',tgeneral='$tgeneral2',descuento='$tdescuento2',itbi='$titbi2',importe='$importe2' where articulo='$articulo2' AND usuario='$usuariop' AND estafeta='$estafeta' AND central='$centralg' ");
 } else {
 
 $tg = $precio2 * $cantidad2;
if(trim($descp)=="" or trim($descd)==""){$tdescuento2=$tg * ((int)$desci/100);} else {$tdescuento2="";}
if ((int)$descp > 0 ){$tdescuento2=$tg * ($descp/100);}
$importe2= $tg-$tdescuento2;
if(trim($impuesto)!="N"){
if(trim($itbiincluido)!="N"){
$titbi2= $importe2-($importe2 / (1+($pitbi/100)));} else {$titbi2= $importe2*($pitbi/100); $importe2=$importe2+$titbi2; $tg=$tg+$titbi2;}
} else {$titbi2="";}
$tgeneral2=$tg-$titbi2;
$pp3 = mysqli_query($link, "INSERT INTO temporal(central,estafeta,usuario,articulo,descripcion,unidad,cantidad,precio,comentario,costo,secuencia,fecha,grupo,zona,suplidor,tgeneral,descuento,itbi,importe) values ('$centralg','$estafeta','$usuariop','$articulo2','$descripcion2','UNIDAD','$cantidad2','$precio2','$comentario2','','$secuencia','$fecha','','','','$tgeneral2' ,'$tdescuento2','$titbi2' ,'$importe2'  )");
$articulo="";$descripcion="";
} }  $tp20=$ordeng;   }
//Fin Transferir Ordenes o Cotizacion 





//Salvar la Factura 
if( trim($paso10)!="" ) {
//if(trim($condicion)=="CONTADO" and $efectivo < 1 and $cheque < 1 $tarjeta < 1 ){$efectivo=$neto;}
$paso10="";
$inicio11 = mysqli_query($link, "select * from master where  codmaster='$estafeta' AND centralmaster='$centralg' ");
$inicio111 = mysqli_num_rows($inicio11);
if ($inicio111 > 0 ){ $inicio21 = mysqli_fetch_array($inicio11);
$factura=$inicio21['factura']; 	$factcre=$inicio21['facturacre'];$factp=$inicio21['factura']+1; $factncf=$inicio21['facturancf'];
$n11p=$inicio21['n11']; $n22p=$inicio21['n22'];	$n33p=$inicio21['n33']; $n44p=$inicio21['n44'];	$estadop=$inicio21['estado'];
	$itbi="";
if( trim($condicion)=="CREDITO") {$factcre=$factcre+1;$estadop=$estadop+1;$factura="00".$factcre;$factp=$inicio21['factura']; }
if (trim($ncf)=="Sin Valor Credito Fiscal") { $factncf= $factncf + 1; $factp=$inicio21['factura']; $n11p=$n11p+1;  $factura="000".$factncf;$itbi=$inicio2['itbi'];}
if (trim($ncf)=="Con Valor Credito Fiscal") { $factncf= $factncf + 1; $factp=$inicio21['factura']; $n22p=$n22p+1;  $factura="000".$factncf;$itbi=$inicio2['itbi'];}
if (trim($ncf)=="Gubernamental")  { $factncf= $factncf + 1; $factp=$inicio21['factura']; $n33p=$n33p+1;  $factura="000".$factncf;$itbi=$inicio2['itbi'];}
if (trim($ncf)=="Regimes Especiales" ) { $factncf= $factncf + 1; $factp=$inicio21['factura']; $n44p=$n44p+1;  $factura="000".$factncf;}
 }
 	$_SESSION['nfactura']=$factp;
	
	
if(trim($factura)==""){echo '<script language="javascript">alert("Atencion... El No.de la Factura esta en Blanco........");</script>'; $centralg=""; $estateta=""; document.getElementById('articulo').focus();   }

//Pedido a Grupo
if(trim($tpedido)=="S"){$codpp=$centralg."-".$estafeta."-".$factura;
 $buscar12 = mysqli_query($link, "select * from temporal where estafeta ='$estafeta' and  central='$centralg' AND  usuario='$usuariop' ORDER BY secuencia DESC  ");
while ($row = mysqli_fetch_assoc($buscar12)){
 $articulo=$row['articulo']; $descripcion=$row['descripcion'];$unidad=$row['unidad'];$cantidad=$row['cantidad'];
$precio=$row['precio'];$comentario=$row['comentario'];$secuencia=$row['secuencia'];$grupo=$row['grupo'];$foto=$row['foto'];

$pp3 = mysqli_query($link, "INSERT INTO pedido(central,estafeta,secuencia,mesa,usuario,articulo,descripcion,precio,cantidad,posteo,fecha,hora,comentario,foto,estado,estado2,grupo) values ('$centralg','$estafeta','$secuencia','$codpp','$usuariop','$articulo','$descripcion','$precio','$cantidad','Pedido','$fecha','$hora','$comentario','$foto','Pedido','Pedido','$grupo')");
 }
}
//Fin de Pedido
  
//Liberar Cotizacion o Ordenes
if(trim($ordeng)!=""){
$buscar1 = mysqli_query($link, "select * from cotizacion where cotizacion ='$ordeng' and posteo ='Normal' and estafeta ='$estafeta' and  central='$centralg' ");
while ($row = mysqli_fetch_assoc($buscar1)){
  $pp11 = mysqli_query($link, "UPDATE cotizacion set posteo='Procesado' where cotizacion ='$ordeng' and posteo ='Normal' and estafeta ='$estafeta' and  central='$centralg'");   }  }
// fin Liberar Cotizacion o Ordenes


$codg=$centralg."-".$estafeta."-".$factura;
$pp111 = mysqli_query($link, "UPDATE master set estado='$estadop',facturacre='$factcre',factura='$factp',facturancf='$factncf',n11='$n11p',n22='$n22p',n33='$n33p',n44='$n44p' where centralmaster='$centralg' and  codmaster='$estafeta' ");

$inicio5 = mysqli_query($link, "select * from cliente where codigo='$cliente' AND estafeta='$estafeta' AND central='$centralg' ");
$inicio15 = mysqli_num_rows($inicio5);if ($inicio15 > 0 ){ $inicio25 = mysqli_fetch_array($inicio5);$zona=$inicio25['zona']; }

$pp31 = mysqli_query($link, "INSERT INTO factura(central,estafeta,factura,cliente,nombre,direccion,telefono,hora,fecha,condicion,diacredito,corden,itbi,descuento,vendedor,ncf,rnc,tgeneral,tdescuento,titbi,importe,cajero,pefectivo,ptarjeta,pcheque,banco,devuelta,placa,camion,zona,flete,cncredito,cierre,posteo,tiponcf,nombree,direccione,telefonoe,camarero,tpley) values ('$centralg','$estafeta','$codg','$cliente','$nombre','$direccion','$telefono','$hora','$fecha','$condicion','','$orden','$itbi','','$vendedor','$ncf1','$rnc','$tgeneral','$tdescuento','$titbi','$neto','$usuariop','$efectivo', '$tarjeta','$cheque','','$devuelta','','','$zona','' ,'','A','Normal','$ncf','$nomenvio','$direnvio' ,'$telenvio','$camarero','$pley' )");

 //busqueda del temporal de articulo
 $buscar12 = mysqli_query($link, "select * from temporal where estafeta ='$estafeta' and  central='$centralg' AND  usuario='$usuariop' ORDER BY secuencia DESC  ");
while ($row = mysqli_fetch_assoc($buscar12)){
 $articulo=$row['articulo']; $descripcion=$row['descripcion'];$unidad=$row['unidad'];$cantidad=$row['cantidad'];
		$precio=$row['precio'];$descuento=$row['descuento'];$itbi=$row['itbi'];$netod=$row['tgeneral'];$imported=$row['importe'];
		$comentario=$row['comentario'];$secuencia=$row['secuencia'];$costo="";$conduce="";$suplidor="";
//Acualizar Existencia en archivo
$inicio4 = mysqli_query($link, "select * from articulos where codigo='$articulo' AND estafeta='$estafeta' AND central='$centralg' ");
$inicio14 = mysqli_num_rows($inicio4);
if ($inicio14 > 0 ){ $inicio24 = mysqli_fetch_array($inicio4);
$existencia=0;$existenciap=0;$exip=0;
$existenciap=$inicio24['existencia']; 
$unidad2=$inicio24['unidad2']; 
$unidad3=$inicio24['unidad3'];
$unidad4=$inicio24['unidad4'];
$caja1=$inicio24['caja1']; $costo=$inicio24['costo']; $suplidor=$inicio24['suplidor']; 
$caja2=$inicio24['caja2'];
$caja3=$inicio24['caja3'];
$grupo=$inicio24['grupo'];$foto=$inicio24['foto'];
$exip=$cantidad;
 if (trim($existenciap)=="") {$existenciap=0.00;} 
 if(trim($unidad)==trim($unidad2) and trim($caja1)!=""){$exip=$cantidad * $caja1;}
 if(trim($unidad)==trim($unidad3) and trim($caja2)!=""){$exip=$cantidad * $caja2;}
 if(trim($unidad)==trim($unidad4) and trim($caja3)!=""){$exip=$cantidad * $caja3;}
$existencia = $existenciap-$exip;	
$pp114 = mysqli_query($link, "UPDATE articulos set existencia='$existencia' where codigo='$articulo' AND  estafeta='$estafeta' AND central='$centralg' ");
 }
//fin Acualizar Existencia en archivo
 //salvar Detalle Factura
 $pp32 = mysqli_query($link, "INSERT INTO detalle(central,estafeta,factura,cliente,articulo,descripcion,unidad,cantidad,precio,descuento,itbi,neto,importe,comentario,
	   costo,secuencia,conduce,grupo,suplidor,fecha,cncredito,altura,ancho,pies,zona,posteo) values ('$centralg','$estafeta','$codg','$cliente','$articulo','$descripcion','$unidad','$cantidad','$precio','$descuento','$itbi',
	   '$netod','$imported','$comentario','$costo','$secuencia','$conduce','$grupo','$suplidor','$fecha','','', '','','$zona','Normal' )");
 }   
 //Factura Crédito
if(trim($condicion)=="CREDITO"){
//balance archivo cliente
$inicio5 = mysqli_query($link, "select * from cliente where codigo='$cliente' AND estafeta='$estafeta' AND central='$centralg' ");
$inicio15 = mysqli_num_rows($inicio5);

if ($inicio15 > 0 ){ $inicio25 = mysqli_fetch_array($inicio5);
$balance=$inicio25['balance']; 

if (trim($balance)=="") {$balance=0.00;}
$balance = $balance+$neto;	
$pp115 = mysqli_query($link, "UPDATE cliente set balance='$balance' where codigo='$cliente' AND estafeta='$estafeta' AND central='$centralg' ");
 }
//actualizar el estado
 $pp35 = mysqli_query($link, "INSERT INTO estado(central,estafeta,estado,cliente,nombre,transaccion,fecha,monto,debito,credito,tipo,factura,recibo,vendedor,ncf,rnc,ncff,posteo) values ('$centralg','$estafeta','$estadop','$cliente','$nombre','Factura Credito','$fecha','$neto','$neto','0.00' ,'Factura','$codg',  '','$vendedor', '$ncf1', '$rnc', '$ncf', 'Normal'   )");
}
$_SESSION['listado3']=$codg;
$_SESSION['listado2'] = $printer;
$ncf= "No Aplica";
if(trim($nomenvio)!=""  and (int)$neto > 0 ){

}  else {

echo "<script type=\"text/javascript\">location.href=\"impfact.php\";</script>"; 

 $cliente="1";$nombre=""; $direccion=""; $telefono=""; $balance=""; $articulo=""; $unidad=""; $descripcion=""; $cantidad=""; $precio="";$neto=""; $importe2=""; $tgeneral=""; $tdescuento=""; $titbi="";
  
 ?> <script language="javascript">  document.getElementById('orden').value= "<?php echo $orden; ?>";
document.getElementById('rnc').value= "<?php echo $rnc; ?>";
document.getElementById('ncf').value= "<?php echo $ncf; ?>";
document.getElementById('ncf1').value= "<?php echo $ncf1; ?>";
document.getElementById('cliente').value= "<?php echo $cliente; ?>";
document.getElementById('clientesep').value= "<?php echo $clientesep; ?>";
</script><?php
echo '<script language="javascript"> document.factrest.submit()</script>';
}}
//Fin Salvar la Factura



//cancelar
if($paso30!="") { 
$borrar1 =  mysqli_query($link,"DELETE FROM temporal WHERE usuario='$usuariop' and estafeta='$estafeta' and central='$centralg'");
$borrar2= mysqli_query($link, "DELETE FROM cabeza where usuario='$usuariop' AND estafeta='$estafeta' AND central='$centralg' ");
$pp12 = mysqli_query($link, "UPDATE pedido set paso='' where estado ='Pedido' and mesa ='$orden' and estafeta ='$estafeta' and  central='$centralg'  ");
  $pp12 = mysqli_query($link, "UPDATE cotizaciondet set paso='' where  cotizacion ='$ordeng' and estafeta ='$estafeta' and  central='$centralg'  ");
$cliente="1";$nombre=""; $direccion=""; $telefono=""; $balance=""; $articulo=""; $unidad=""; $descripcion=""; $cantidad=""; $precio="";$neto=""; $importe2=""; $tgeneral=""; $tdescuento=""; $titbi="";$rnc="";$rncf1="";$orden="";$ncf=""; $ordeng=""
?> <script language="JavaScript">document.factrest.submit(); </script><?php  }
//fin cancelar

//control de clave de Seguridad de Entrada a funciones
if(trim($clavepp2)!="") {
   $inicio = mysqli_query($link, "select * from login where password='$clavepp2' and central='$centralg'  ");
	$inicio11 = mysqli_num_rows($inicio);
	if ($inicio11 > 0 ){ $inicio1 = mysqli_fetch_array($inicio);	
		$tipo50  = $inicio1['type'];
		if (trim($tipo50)=="MASTER" OR trim($tipo50)=="ADMINISTRADOR" OR trim($tipo50)=="OPERADOR"  ){
		echo "<script type=\"text/javascript\">location.href=\"cierre.php\";</script>"; 
		} else {echo '<script language="javascript">alert("Atencion... Usuario  NO Autorizado.....");</script>';}
		
 }else {echo '<script language="javascript">alert("Atencion... Usuario  NO Autorizado.....");</script>';} }
 
 if(trim($clavepp3)!="") {
   $inicio = mysqli_query($link, "select * from login where password='$clavepp3' and central='$centralg'  ");
	$inicio11 = mysqli_num_rows($inicio);
	if ($inicio11 > 0 ){ $inicio1 = mysqli_fetch_array($inicio);	
		$tipo50  = $inicio1['type'];
		if (trim($tipo50)=="MASTER" OR trim($tipo50)=="ADMINISTRADOR" OR trim($tipo50)=="OPERADOR"  ){
		echo "<script type=\"text/javascript\">location.href=\"cuadre001.php\";</script>"; 
		} else {echo '<script language="javascript">alert("Atencion... Usuario  NO Autorizado.....");</script>';}
	 }else {echo '<script language="javascript">alert("Atencion... Usuario  NO Autorizado.....");</script>';}  }
 if(trim($clavepp)!="") {
   $inicio = mysqli_query($link, "select * from login where password='$clavepp' and central='$centralg'  ");
	$inicio11 = mysqli_num_rows($inicio);
	if ($inicio11 > 0 ){ $inicio1 = mysqli_fetch_array($inicio);	
		$tipo50  = $inicio1['type'];
		if (trim($tipo50)=="MASTER" OR trim($tipo50)=="ADMINISTRADOR" OR trim($tipo50)=="OPERADOR"  ){
		echo '<script language="javascript">anular01();</script>';
		} else {echo '<script language="javascript">alert("Atencion... Usuario  NO Autorizado.....");</script>';}
		
 }else {echo '<script language="javascript">alert("Atencion... Usuario  NO Autorizado.....");</script>';} }
 //Fin de Autorizacion de Funciones
 
//cambio de precio
if(trim($paso40)!="") {
$inicio = mysqli_query($link,"select * from articulos where codigo='$articulo' AND estafeta='$estafeta' AND central='$centralg'");
	$inicio1 = mysqli_num_rows($inicio);
	if ($inicio1 > 0 ){ $inicio2 = mysqli_fetch_array($inicio);	
        $existencia = $inicio2['existencia'];
		 $precio = $inicio2['precio1'];
	     $preciom = $inicio2['precio2'];
		 $unidad2=$inicio2['unidad2']; 
         $unidad3=$inicio2['unidad3'];
         $unidad4=$inicio2['unidad4'];
         $caja1=$inicio2['caja1']; 
         $caja2=$inicio2['caja2'];
         $caja3=$inicio2['caja3'];$foto=$inicio2['foto'];$grupo=$inicio2['grupo'];
		 
         if (trim($existencia)=="") {$existencia=0.00;} 
if(trim($unidad)==trim($unidad2) and trim($caja1)!=""){$existencia=$existencia / $caja1;$preciom = $inicio2['precio4'];		 $precio = $inicio2['precio3'];}
if(trim($unidad)==trim($unidad3) and trim($caja2)!=""){$existencia=$existencia / $caja2;$preciom = $inicio2['precio6'];		 $precio = $inicio2['precio5'];}
if(trim($unidad)==trim($unidad4) and trim($caja3)!=""){$existencia=$existencia / $caja3;$preciom = $inicio2['precio8'];		 $precio = $inicio2['precio7'];}
 $existencia = bcadd($existencia,'0',2);
 } }
//fin cambio de precio

$importe2=bcadd($neto,'0',2);
if( trim($efectivo)!=""){ $efectivo = bcadd($efectivo,'0',2); $tdg= $efectivo+$cheque+$tarjeta;
 if ($importe2 > $tdg){$devuelta="0.00";} else { $devuelta = bcadd(($efectivo+$cheque+$tarjeta)-$importe2,'0',2); }}

if(isset($_POST['busqueda1'])) { $valor1 = $_POST['busqueda1']; $valor2=split("[=]",$valor1); $valor3=$valor2[0]; if($valor3!="BUSQUEDA"){ $cliente=$valor2[0]; $cliente =strtoupper(trim($cliente)); $nombre ="";
  ?>  <script language="JavaScript">   document.getElementById('cliente').value= "<?php echo $cliente; ?>"; 	</script>    <?php 
}} 
//Validar el monto solo numero
  $str = strlen($cantidad);
  $x = 0;$p1t="S";$p=$cantidad;
  while ($x < $str){$k1 = substr($p, $x, 1);if($k1!="0" and  $k1!="1"  and  $k1!="2" and  $k1!="3" and  $k1!="4" and  $k1!="5"  and  $k1!="6"and  $k1!="7" and  $k1!="8"  and  $k1!="9"and $k1!="."){$p1t="N";$x=$str;$cantidad="";} $x++;}
 //fin de la validacion
//Validar el monto solo numero
  $str = strlen($precio);
  $x = 0;$p1t="S";$p=$precio;
  while ($x < $str){$k1 = substr($p, $x, 1);if($k1!="0" and  $k1!="1"  and  $k1!="2" and  $k1!="3" and  $k1!="4" and  $k1!="5"  and  $k1!="6"and  $k1!="7" and  $k1!="8"  and  $k1!="9"and $k1!="."){$p1t="N";$x=$str;$precio="";} $x++;}
 //fin de la validacion
 
 //Validar el monto solo numero
  $str = strlen($desci);
  $x = 0;$p1t="S";$p=$desci;
  while ($x < $str){$k1 = substr($p, $x, 1);if($k1!="0" and  $k1!="1"  and  $k1!="2" and  $k1!="3" and  $k1!="4" and  $k1!="5"  and  $k1!="6"and  $k1!="7" and  $k1!="8"  and  $k1!="9"and $k1!="."){$p1t="N";$x=$str;$desci="";} $x++;}
 //fin de la validacion
//Validar el monto solo numero
  $str = strlen($descp);
  $x = 0;$p1t="S";$p=$descp;
  while ($x < $str){$k1 = substr($p, $x, 1);if($k1!="0" and  $k1!="1"  and  $k1!="2" and  $k1!="3" and  $k1!="4" and  $k1!="5"  and  $k1!="6"and  $k1!="7" and  $k1!="8"  and  $k1!="9"and $k1!="."){$p1t="N";$x=$str;$descp="";} $x++;}
 //fin de la validacion
//Validar el monto solo numero
  $str = strlen($descd);
  $x = 0;$p1t="S";$p=$descd;
  while ($x < $str){$k1 = substr($p, $x, 1);if($k1!="0" and  $k1!="1"  and  $k1!="2" and  $k1!="3" and  $k1!="4" and  $k1!="5"  and  $k1!="6"and  $k1!="7" and  $k1!="8"  and  $k1!="9"and $k1!="."){$p1t="N";$x=$str;$descd="";} $x++;}
 //fin de la validacion
  $balance = bcadd($balance,'0',2);
// Visualizar Cabeza
if (trim($cliente)=="" ){
$inicio = mysqli_query($link, "select * from cabeza where usuario='$usuariop' AND estafeta='$estafeta' AND central='$centralg' ");
	$inicio1 = mysqli_num_rows($inicio);
	if ($inicio1 > 0 ){ $inicio2 = mysqli_fetch_array($inicio);	
         $ncf = $inicio2['ncf'];
		  $rnc = $inicio2['rnc'];
		 $ncf1 = $inicio2['ncf1'];
		 $condicion = $inicio2['condicion'];
         $vendedor = $inicio2['vendedor'];
		 $orden = $inicio2['orden'];
		 $cliente = $inicio2['cliente'];
	     $nombre = $inicio2['nombre'];
		 $direccion = $inicio2['direccion'];
		 $telefono = $inicio2['telefono'];
         $balance = $inicio2['balance'];
		 $printer = $inicio2['printer'];
	  }}
// fin Visualziar cabeza
// validar porciento
 if ((int)$descd > 0 ){$p1= (int)$neto/10;if ((int)$descd > $p1){echo'<script language="javascript">alert("Atencion.El Descuento Mayor 10 Por.");</script>';$descp="";$desci="";$descd="";}}
 if ((int)$descp >10 or (int)$desci > 10){echo '<script language="javascript">alert("Atencion... El Descuento No puede Ser Mayor al 10 Porciento....");</script>';$descp="";$desci="";}
 if ((int)$tdescuento < 1 ){  if ((int)$descd> 0 ){$tdescuento=$descd;$neto= $neto-$tdescuento;} if ((int)$descp> 0 ){$tdescuento=$neto * ($descp/100);$neto= $neto-$tdescuento;} }
// fin validar porciento


// Buscar el master
	$inicio = mysqli_query($link, "select * from master where  codmaster='$estafeta' AND centralmaster='$centralg' ");
	$inicio1 = mysqli_num_rows($inicio);
	if ($inicio1 > 0 ){ $inicio2 = mysqli_fetch_array($inicio);	
           $factura = $inicio2['factura'];
		   $pitbi=0.00;
           if (trim($ncf)=="Sin Valor Credito Fiscal") { $ncf1=$inicio2['n1'].$inicio2['n11'];$pitbi=$inicio2['itbi']; $factura=             "000".$inicio2['facturancf'];}
		   if (trim($ncf)=="Con Valor Credito Fiscal") {$ncf1=$inicio2['n2'].$inicio2['n22']; $pitbi=$inicio2['itbi']; $factura=             "000".$inicio2['facturancf'];}
		   if (trim($ncf)=="Gubernamental") {$ncf1=$inicio2['n3'].$inicio2['n33'];$pitbi=$inicio2['itbi']; $factura=             "000".$inicio2['facturancf'];}
		   if (trim($ncf)=="Regimes Especiales" ){$ncf1=$inicio2['n4'].$inicio2['n44'];$factura="000".$inicio2['facturancf'];}
	       if (trim($ncf)=="No Aplica") {$ncf1='';}	
		   	$_SESSION['nfactura']=$factura;
		   //Itbi a a factura 
		   if (trim($ncf)!="No Aplica" and  trim($ncf1p)=="" and $neto > 0  ) {
$tdp5=0.00;
 $buscar1 = mysqli_query($link, "select * from temporal where estafeta ='$estafeta' and  central='$central' AND  usuario='$usuariop' ORDER BY secuencia DESC  ");
while ($rowg = mysqli_fetch_assoc($buscar1)){
$impgg = $rowg['cantidad'] * $rowg['precio']; $secgg = $rowg['secuencia'];$artg = $rowg['articulo']; $impuestog="";
$iniciog = mysqli_query($link, "select * from articulos where codigo='$artg' AND estafeta='$estafeta' AND central='$centralg' ");
$inicio1g = mysqli_num_rows($iniciog);
	if ($inicio1g > 0 ){ $inicio2g = mysqli_fetch_array($iniciog); $impuestog = $inicio2g['impuesto'];}

if ((int)$descp > 0 ){$tdp5=$impgg * ($descp/100);$impgg= $impgg-$tdp5;}
if(trim($impuestog)!="N"){
if(trim($itbiincluido)!="N"){$itgg= $impgg - ( $impgg / (1+($pitbi/100)));}
else {$itgg=  $impgg * ($pitbi/100); $impgg=$impgg+$itgg; }
} else {$itgg="";}

$tbp5=($impgg+$tdp5)-$itgg;
$pp11 = mysqli_query($link, "UPDATE temporal set tgeneral='$tbp5',itbi='$itgg',descuento='$tdp5',importe='$impgg'  where central='$centralg' and usuario='$usuariop' AND estafeta='$estafeta'  and secuencia='$secgg' ");
 } 
  ?> <script language="JavaScript">document.getElementById('articulo').focus();</script>  <?php 
  }	
 // fin colocar itbi
    }
//	fin master
 $_SESSION['lista1'] =$articulo;
 $_SESSION['lista2'] =$tdescuento;
  // Buscar el Articulo
if (trim($articulo)!="" and trim($descripcion)==""){
$inicio = mysqli_query($link,"select * from articulos where codigo='$articulo' AND estafeta='$estafeta' AND central='$centralg'");
$inicio1 = mysqli_num_rows($inicio);
 if ($inicio1 > 0 ){ $inicio2 = mysqli_fetch_array($inicio);	
    $descripcion = $inicio2['descripcion'];$existencia = $inicio2['existencia'];$precio = $inicio2['precio1'];$grupo = $inicio2['grupo'];
	$foto = $inicio2['foto'];$preciom = $inicio2['precio2']; $impuesto = $inicio2['impuesto']; $impuestoley = $inicio2['impuestol'];$unidad = $inicio2['unidad'];$unidad2=$inicio2['unidad2'];$unidad3=$inicio2['unidad3'];$unidad4=$inicio2['unidad4'];$caja1=$inicio2['caja1'];$caja2=$inicio2['caja2'];$caja3=$inicio2['caja3'];
         if (trim($existencia)=="") {$existencia=0.00;} 
if(trim($unidad)==trim($unidad2) and trim($caja1)!=""){$existencia=$existencia / $caja1;$preciom = $inicio2['precio4'];		 $precio = $inicio2['precio3'];}
if(trim($unidad)==trim($unidad3) and trim($caja2)!=""){$existencia=$existencia / $caja2;$preciom = $inicio2['precio6'];		 $precio = $inicio2['precio5'];}
if(trim($unidad)==trim($unidad4) and trim($caja3)!=""){$existencia=$existencia / $caja3;$preciom = $inicio2['precio8'];		 $precio = $inicio2['precio7'];}
  }	}
// fin busqueda articulo


  ?>  <script language="JavaScript">
    document.getElementById('montorec').value= "<?php echo $montorec; ?>";
  document.getElementById('nomrec').value= "<?php echo $nomrec; ?>";
document.getElementById('direc').value= "<?php echo $direc; ?>";
document.getElementById('telrec').value= "<?php echo $telrec; ?>";
    document.getElementById('tp20').value= "<?php echo $tp20; ?>";
	   document.getElementById('ley').value= "<?php echo $ley; ?>";
	    document.getElementById('pley').value= "<?php echo $pley; ?>"; 
  document.getElementById('tpedido').value= "<?php echo $tpedido; ?>";
    document.getElementById('foto').value= "<?php echo $foto; ?>";
    document.getElementById('grupo').value= "<?php echo $grupo; ?>";
document.getElementById('orden').value= "<?php echo $orden; ?>";
document.getElementById('ordeng').value= "<?php echo $ordeng; ?>";
document.getElementById('nfactura').value= "<?php echo $factura; ?>";
document.getElementById('rnc').value= "<?php echo $rnc; ?>";
document.getElementById('ncf').value= "<?php echo $ncf; ?>";
document.getElementById('ncf1').value= "<?php echo $ncf1; ?>";
document.getElementById('cliente').value= "<?php echo $cliente; ?>";
document.getElementById('clientesep').value= "<?php echo $clientesep; ?>";
document.getElementById('nombre').value= "<?php echo $nombre; ?>";
document.getElementById('direccion').value= "<?php echo $direccion; ?>";
document.getElementById('telefono').value= "<?php echo $telefono; ?>";
document.getElementById('printerp').value= "<?php echo $printer; ?>";
document.getElementById('condicion').value= "<?php echo $condicion; ?>";
document.getElementById('vendedor').value= "<?php echo $vendedorp2; ?>";
document.getElementById('ncf').value= "<?php echo $ncf; ?>";
document.getElementById('descp').value= "<?php echo $descp; ?>";
document.getElementById('descd').value= "<?php echo $descd; ?>";
document.getElementById('comentario').value= "<?php echo $comentario; ?>";
document.getElementById('tgeneral').value= "<?php echo $tgeneral; ?>";
document.getElementById('tdescuento').value= "<?php echo $tdescuento; ?>";
document.getElementById('titbi').value= "<?php echo $titbi; ?>";
document.getElementById('importe2').value= "<?php echo $importe2; ?>";
document.getElementById('neto').value= "<?php echo $neto; ?>";
document.getElementById('efectivo').value= "<?php echo $efectivo; ?>";
document.getElementById('devuelta').value= "<?php echo $devuelta; ?>";
document.getElementById('cheque').value= "<?php echo $cheque; ?>";
document.getElementById('tarjeta').value= "<?php echo $tarjeta; ?>";
document.getElementById('nomenvio').value= "<?php echo $nomenvio; ?>";
document.getElementById('direnvio').value= "<?php echo $direnvio; ?>";
document.getElementById('telenvio').value= "<?php echo $telenvio; ?>";
document.getElementById('envio1').value= "<?php echo $envio1; ?>";
document.getElementById('descripcion').value= "<?php echo $descripcion; ?>";
document.getElementById('unidad').value= "<?php echo $unidad; ?>";
document.getElementById('precio').value= "<?php echo $precio; ?>";
document.getElementById('itbiincluido').value= "<?php echo $itbiincluido; ?>";

</script>     <?php
 
// Salvar la Factura
 if(trim($articulo)=="*" and (int)$neto > 0 ){ 
 ?>  <script language="javascript" type="text/javascript">
   document.all.item("pantalla01").style.visibility='visible' 
 </script
  ><?php 
  
   if($importe2!="" and $efectivo==""){?> <script language="JavaScript">
   document.getElementById('efectivo').focus(); <?php } ?>  </script>  <?php  
  
     if($importe2!="" and $efectivo!="" and $devuelta!=""){?>
  <script language="JavaScript">
   document.getElementById('salvarp').focus(); <?php } ?>
  	  </script>  
     <?php 
   if($importe2!="" and $efectivo!="" and $devuelta==""){?>
  <script language="JavaScript">
   document.getElementById('devuelta').focus(); <?php } ?>
  	  </script>  
     <?php  


	}
// fin salvar Factira
// Envio 
   if(trim($envio1)!=""  and (int)$neto > 0 ){
?>  
  <script language="javascript" type="text/javascript">
   document.all.item("pantalla02").style.visibility='visible' </script> <?php 
   if($importe2!="" and $nomenvio==""){?> <script language="JavaScript">
   document.getElementById('nomenvio').focus(); <?php } ?>  </script>  <?php  
   if( $importe2!="" and $nomenvio!="" and $direnvio==""){?> <script language="JavaScript">
   document.getElementById('direnvio').focus(); <?php } ?>  </script>  <?php  
   if( $importe2!="" and $nomenvio!="" and $direnvio!="" and $telenvio==""  ){?> <script language="JavaScript">
   document.getElementById('telenvio').focus(); <?php } ?>  </script>  <?php  
	}
 //fin Envio
 
if (trim($descripcion)=="" and $articulo!="*"){$articulo="";}

// Buscar el Cliente
if ($cliente!="1" and  $nombre=="CONTADO"){$nombre="";}
if ($cliente!="" and  $nombre==""){
$existe="N";
$inicio = mysqli_query($link, "select * from cliente where codigo='$cliente' AND estafeta='$estafeta' AND central='$centralg' ");
	$inicio1 = mysqli_num_rows($inicio);
	if ($inicio1 > 0 ){ $inicio2 = mysqli_fetch_array($inicio);	
         $nombre = $inicio2['nombre'];
		 $rnc = $inicio2['rnc'];
		 $direccion = $inicio2['direccion'];
		 $telefono = $inicio2['telefono'];
         $balance = $inicio2['balance'];
		  $balance = bcadd($balance,'0',2);
		  $existe="S";
	  }  
 if ($existe=="N" and  $cliente!=""){
 echo '<script language="javascript">alert("Atencion... Esta Codigo de Cliente No Registrado en el Archivo.....");</script>';$cliente="";
  } }
// fin busqueda clietne
   // Agregart el Item Factura
$paso=trim(substr($paso,0,1));
if($articulo!="" and $cantidad!="" and $precio!="" and $paso=="S" ) {

$inicio111 = mysqli_query($link,"select * from  temporal where  articulo='$articulo' AND usuario='$usuariop' AND estafeta='$estafeta' AND central='$centralg'  ");
$inicio121 = mysqli_num_rows($inicio111);if ($inicio121 > 0 ){$inicio221 = mysqli_fetch_array($inicio111);
$canpp= $inicio221['cantidad'];	$prepp= $inicio221['precio'];	
$balp1=$canpp*$prepp;  $balp2=$cantidad*$precio;  $cantidad=$canpp+$cantidad; $precio= ($balp1+$balp2)/$cantidad; }


$tg = $precio * $cantidad;

if(trim($descp)=="" or trim($descd)==""){$tdescuento2=$tg * ((int)$desci/100);} else {$tdescuento2="";}
if ((int)$descp > 0 ){$tdescuento2=$tg * ($descp/100);}
$importe2= $tg-$tdescuento2;

if(trim($impuesto)!="N"){

if(trim($itbiincluido)!="N"){
$titbi2= $importe2-($importe2 / (1+($pitbi/100)));} else {$titbi2= $importe2*($pitbi/100); $importe2=$importe2+$titbi2; $tg=$tg+$titbi2;}
} else {$titbi2="";}
$secuencia=$centralg.$estafeta.$usuariop.$fecha.substr($secu,0,2).substr($secu,3,2).substr($secu,6,2);
$tgeneral2=$tg-$titbi2;
//porciento de ley en bar
$ipley=$_SESSION['listado6'];$ppley=0;
if(trim($ley)!="" and trim($impuestoley)!="N"){
 if(trim($ipley)=="S"){
$ppley= $importe2- ($importe2/(1+ ($ley/100))); } else {$ppley= $importe2 * ($ley/100); $importe2=$importe2+$ppley;} 
 $ppley = bcadd($ppley,'0',2); }
//fin porciento de bar   
$inicio111 = mysqli_query($link,"select * from  temporal where  articulo='$articulo' AND usuario='$usuariop' AND estafeta='$estafeta' AND central='$centralg'  ");
$inicio121 = mysqli_num_rows($inicio111);
if ($inicio121 > 0 ){
$inicio221 = mysqli_fetch_array($inicio111);
$pp11 = mysqli_query($link, "UPDATE temporal set cantidad='$cantidad',unidad='$unidad',precio='$precio',tgeneral='$tgeneral2',descuento='$tdescuento2',itbi='$titbi2',pley='$ppley',importe='$importe2' where  articulo='$articulo' AND central='$centralg' and usuario='$usuariop' AND estafeta='$estafeta' ");
	}  else {
$pp3 = mysqli_query($link, "INSERT INTO temporal(central,estafeta,usuario,articulo,descripcion,unidad,cantidad,precio,comentario,costo,secuencia,fecha,grupo,zona,suplidor,tgeneral,descuento,itbi,importe,foto,pley) values ('$centralg','$estafeta','$usuariop','$articulo','$descripcion','$unidad','$cantidad','$precio','$comentario','$costo','$secuencia','$fecha','$grupo','$zona','$suplidor','$tgeneral2' ,'$tdescuento2','$titbi2','$importe2','$foto','$ppley' )");
} 

$articulo='';$cantidad=''; $precio = '';  $descripcion = ''; $paso="";$_SESSION['lista1'] ="";$comentario="";$desci=0;

	   }		
// fin Agregar Item Factura

// Agregart el cabeza
if ($cliente!="" and  $nombre!="" and  $nombre!="CONTADO" and  $cliente!="1"  ){
 $inicio11 = mysqli_query($link, "select * from  cabeza where  usuario='$usuariop' AND estafeta='$estafeta' AND central='$centralg'  ");
$inicio12 = mysqli_num_rows($inicio11);
if ($inicio12 > 0 ){
$inicio22 = mysqli_fetch_array($inicio11);	
 $pp11 = mysqli_query($link, "UPDATE cabeza set factura='$factura',cliente='$cliente',nombre='$nombre',direccion='$direccion',telefono='$telefono',balance='$balance' ,vendedor='$vendedor',orden='$orden',condicion='$condicion',printer='$printer',rnc='$rnc',ncf='$ncf',ncf1='$ncf1',tgeneral='$tgeneral',descuento='$tdescuento',itbi='$titbi',tpley='$pley',importe='$neto' where central='$centralg' and usuario='$usuariop' AND estafeta='$estafeta' ");
	}  else {
	$pp3 = mysqli_query($link, "INSERT INTO cabeza(central,estafeta,usuario,factura,cliente,nombre,direccion,telefono,balance,vendedor,orden,condicion,printer,rnc,ncf,ncf1,tgeneral,descuento,itbi,tpley,importe) values ('$centralg','$estafeta','$usuariop','$factura','$cliente','$nombre','$direccion','$telefono','$balance','$vendedor','$orden','$condicion','$printer','$rnc','$ncf','$ncf1','$tgeneral','$tdescuento','$titbi','$pley','$neto' )");
	}}
// fin cabeza el cabeza


// Busqueda de articulo 
if ($bnombre2!="" ){  ?>	
	</table>
 <div style="overflow: auto; width: 100%; height: 650px;">
<table class="table table-striped table-bordered table-hover table-condensed"  style="font-size:12px" >
   <tr class="info">
    <th width="50" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Codigo</span></th>
    <th width="250" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Descripcion</span></th>
	<th width="50" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Unidad</span></th>
    <th width="50" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Referencia</span></th>
	<th width="50" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Existencia</span></th>
	<th width="50" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Precio1</span></th>
	<th width="50" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Precio2</span></th>
	<th width="50" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Precio3</span></th>
	<th width="50" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Suplidor</span></th>
	<th width="50" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Grupo</span></th>
    <th width="60" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Elegir</span></th>
   </tr>
  <tr>
	 <?php 
	  $buscar1 = mysqli_query($link, "select * from articulos where estafeta ='$estafeta' and  central='$centralg' AND  CONCAT(codigo,'',codbarra,'',descripcion,'',unidad,'',unidad2,'',referencia,'',ubicacion,'',suplidor,'',grupo)  LIKE '%".$bnombre2."%'  ");
while ($row = mysqli_fetch_assoc($buscar1)){
  	   	$tt=$tt+1;
		$condicion="";
			 $xx="";
	echo '<b><tr class="'.$xx.'" class="active" >';
		$secpp=$row['codigo'].'='.$cliente.'='.$orden.'='.$ncf.'='.$ncf1.'='.$condicion.'='.$vendedorp2.'='.$camarero.'='.$clientesep;
        echo '<td><b>'.$row['codigo'].'</td>';
		echo '<td><b>'.$row['descripcion'].'</td>';
	    echo '<td><b>'.$row['unidad'].'</td>';
	    echo '<td><b>'.$row['referencia'].'</td>';
	    echo '<td><b>'.$row['precio1'].'</td>';
	    echo '<td><b>'.$row['precio2'].'</td>';
		echo '<td><b>'.$row['precio3'].'</td>';
	    echo '<td><b>'.$row['precio4'].'</td>';
		echo '<td><b>'.$row['suplidor'].'</td>';
		echo '<td><b>'.$row['grupo'].'</td>';
		echo'<td><b>'.'<a href="factura.php?visualizar2='.$secpp.'" ><img src="../rifamovil/images/edit.png" width="35"  height="20" title="  Comentario de Item" /></a>'.'</td>';
   	   echo'</tr>';
   	}
    echo'</table>';
	echo '</div>';
	$gastop = number_format($gastop,2);
	$tot= number_format($tot,2);
	$mont = number_format($mont,2);
	echo'<h5><b>TOTAL ARTICULOS&nbsp;&nbsp :&nbsp;&nbsp'.$tt.'</b></h5>';
	}
// Fin de la Busqueda articulo


// Busqueda de padres o estudiantes 
if ($bnombre!="" ){
	 ?>	
  </table>
 
 <div style="overflow: auto; width: 100%; height: 650px;">
<table class="table table-striped table-bordered table-hover table-condensed"  style="font-size:12px" >

  <tr class="info">
    <th width="5" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Codigo</span></th>
    <th width="50" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Nombre</span></th>
	<th width="50" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Direccion</span></th>
    <th width="50" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Telefono</span></th>
	<th width="50" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Representante</span></th>
	<th width="50" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Telefono</span></th>
	<th width="5" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Zona</span></th>
	<th width="5" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Vendedor</span></th>
	<th width="5" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Balance</span></th>
    <th width="5" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Elegir</span></th>
  </tr>
  <tr>
	 <?php 
	 
	  $buscar1 = mysqli_query($link, "select * from cliente where estafeta ='$estafeta' and  central='$centralg' AND  CONCAT(codigo,'',nombre,'',direccion,'',telefono,'',correo,'',representante,'',telefonor,'',zona,'',vendedor)  LIKE '%".$bnombre."%'  ");
		 
while ($row = mysqli_fetch_assoc($buscar1)){
  	   	$posteo  = $row['posteo'];
		$tt=$tt+1;
        $mont += $row['balance'];
		 $secpp=$row['codigo'].'='.$orden.'='.$ncf.'='.$ncf1.'='.$condicion.'='.$vendedorp2.'='.$camarero.'='.$clientesep;
	   	 $xx="";
	echo '<b><tr class="'.$xx.'" class="active" >';
        echo '<td><b>'.$row['codigo'].'</td>';
		echo '<td><b>'.$row['nombre'].'</td>';
	    echo '<td><b>'.$row['direccion'].'</td>';
	    echo '<td><b>'.$row['telefono'].'</td>';
	    echo '<td><b>'.$row['representante'].'</td>';
	    echo '<td><b>'.$row['telefonor'].'</td>';
		echo '<td><b>'.$row['zona'].'</td>';
	    echo '<td><b>'.$row['vendedor'].'</td>';
		echo '<td><b>'.$row['balance'].'</td>';
		echo'<td><b>'.'<a href="factura.php?visualizar='.$secpp.'" ><img src="../rifamovil/images/edit.png" width="35"  height="20" title="  Comentario de Item" /></a>'.'</td>';
   	   echo'</tr>';
   	}
    echo'</table>';
	echo '</div>';
	$gastop = number_format($gastop,2);
	$tot= number_format($tot,2);
	$mont = number_format($mont,2);
	echo'<h5><b>TOTAL CLIENTE&nbsp;&nbsp :&nbsp;&nbsp'.$tt.'</b></h5>';
	}
// Fin de la Busqueda

?>

<div class="container"> 
<div style="overflow: auto; width: 100%; height: 250px;">
<table class="table table-striped table-bordered table-hover table-condensed"  style="font-size:12px" >
    <tr class="info">
    <th width="40" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Codigo</span></th>
    <th width="200" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Descripcion</span></th>
    <th width="50" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Unidad</span></th>
   <th width="50" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Cantidad</span></th>
   <th width="50" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Precio</span></th>
   <th width="70" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Tot.Gral.</span></th>
   <th width="50" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Descuento</span></th>
   <th width="50" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Itbis</span></th>
    <th width="70" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Importe</span></th>
   <th width="50" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Comentario</span></th>
    <th width="50" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">B.Item</span></th>
  </tr>  
   <?php 

$central =$_SESSION['central'];
$estafeta =$_SESSION['estafeta'];
$usuario=$_SESSION['usuario'];
$tgeneral =0.00;$tdescuento =0.00;$titbi =0.00;$neto =0.00;$pley =0.00;
//Listaa articulos registrado factura
 $buscar1 = mysqli_query($link, "select * from temporal where estafeta ='$estafeta' and  central='$central' AND  usuario='$usuario' ORDER BY secuencia DESC  ");
while ($row = mysqli_fetch_assoc($buscar1)){
	$tgeneral  = $tgeneral+$row['tgeneral'];
	$tdescuento  = $tdescuento+$row['descuento'];
	$secpp=$row['secuencia'].'='.$cliente.'='.$orden.'='.$ncf.'='.$ncf1.'='.$condicion.'='.$vendedorp2.'='.$camarero.'='.$clientesep;
	$titbi  = $titbi+$row['itbi'];
	$pley  = $pley+$row['pley'];
	$neto  =$neto+$row['importe'];
	 $xx="";
	echo '<b><tr class="'.$xx.'" class="active" >';
        echo '<td><b>'.$row['articulo'].'</td>';
		echo '<td><b>'.$row['descripcion'].'</td>';
	    echo '<td><b>'.$row['unidad'].'</td>';
		echo '<td><b>'.number_format($row['cantidad'],2).'</td>';
	    echo '<td><b>'.number_format($row['precio'],2).'</td>';
    	echo '<td><b>'.number_format($row['tgeneral'],2).'</td>';
	    echo '<td><b>'.number_format($row['descuento'],2).'</td>';
		echo '<td><b>'.number_format($row['itbi'],2).'</td>';
      	echo '<td><b>'.number_format($row['importe'],2).'</td>';
	    echo '<td><b>'.$row['comentario'].'</td>';
	   	echo'<td><b>'.'<a href="factura.php?borrar='.$secpp.'" ><img src="../facturacom/images/borrar.png" width="25"  height="15" title="  Borrar Item Factura" /></a>'.'</td>';

		
   	   echo'</tr>';
	 } 
 $tgeneral = bcadd($tgeneral,'0',2);
 $tdescuento = bcadd($tdescuento,'0',2);
 $titbi = bcadd($titbi,'0',2);
 $pley = bcadd($pley,'0',2);
 $neto = bcadd($neto,'0',2);
   
   ?>
  </table>
  </div>
    <table class="table table-striped table-bordered table-hover table-condensed " >
 </div>
 <footer>
 <div class="container"> 

  <tr class="info">
  <td>   </td>
  </tr>
  <tr class="info">
  <div class="col-xs-12 col-lg-8" > 
  <th>  
 <button class="btn btn-sm btn-primary" id="at" name="at" value="A" type="button" onclick="anulart1();" >Anular</button>
 <button class="btn btn-sm btn-primary" id="cuadre" name="cuadre" value="cuadre" type="button" onclick="cuadre1();" >Cdre Cja</button>
  <button class="btn btn-sm btn-primary" id="gana" name="nrep" value="nrep" type="button" onclick="recibo01();" >Recibo Ingreso</button>
 <button class="btn btn-sm btn-primary" id="inicio" name="inicio" value="" type="button" onclick="inicio1();" >Inicio Cja</button>
 <button class="btn btn-sm btn-primary" id="canelarp" name="cancelarp" value="CANCELAR" type="button" onclick="cancelar02();" >Cancelar</button>
<button class="btn btn-sm btn-success" id="reimprimir" name="reimprimir" value=" RI" type="button" onclick="reimprimir1();">ReImp</button>



<button class="btn btn-sm btn-success" id="125" name="125" value="125" type="submit"  >Procesar</button>
 <button class="btn btn-sm btn-warning" id="salir" name="salir" value="Salir" type="button"  onclick="salir1();" >Salir</button>
 Desc.%:
   <input class="info" style="font-size:12px"  name="descp" type="text" placeholder=""  id ="descp" value="" size="1" />
RD$:
 <input class="info" style="font-size:12px"  name="descd" type="text" placeholder=""  id ="descd" value="" size="1" /> 
 %Ley:
 <input class="info" style="font-size:12px"  name="ley" type="text" placeholder=""  id ="ley" value="" size="1" /> 

   </th>
   </div>   
</tr>  
<tr class="info">
   <th> 
   <input style="font-size:12px"  size="35" type="text" name="bnombre2" placeholder="Buscar Articulo"  value="" id="bnombre2"   >
    &nbsp;&nbsp;
      T.Gral $:
   <input style="font-size:14px"  size="6" type="text" name="tgeneral"   value="0.00" id="tgeneral"  READONLY >
  &nbsp;&nbsp;  T.Desc.$:
  <input style="font-size:14px"  size="6" type="text" name="tdescuento"   value="0.00" id="tdescuento"  readonly /> 
  &nbsp;&nbsp; T.Itbis $:
   <input style="font-size:14px"  size="6" type="text" name="titbi"   value="0.00" id="titbi"   READONLY >
    &nbsp;&nbsp; T.%Ley$:
   <input style="font-size:14px"  size="6" type="text" name="pley"   value="0.00" id="pley"   READONLY >

   

    </th>
</tr>     
<tr class="info">
   <th > 
      <input  size="10"  type="hidden" name="tp20"   value="" id="tp20"   READONLY >
      <input  size="10"  type="hidden" name="delip"   value="" id="delip"   READONLY >
   <input  size="10"  type="hidden" name="clavepp3"   value="" id="clavepp3"   READONLY >
   	 <input  size="10"  type="hidden" name="clavepp2"   value="" id="clavepp2"   READONLY >
   	 <input  size="10"  type="hidden" name="clavepp"   value="" id="clavepp"   READONLY >
     <input  size="10"  type="hidden" name="envio1"   value="" id="envio1"   READONLY >
      <input  size="10"  type="hidden" name="paso"   value="" id="paso"   READONLY >
	  <input  size="10"  type="hidden" name="paso10"   value="" id="paso10"   READONLY >
	  <input  size="10"  type="hidden" name="paso11"   value="" id="paso11"   READONLY >
	  <input  size="10"  type="hidden" name="paso20"   value="" id="paso20"   READONLY >
	    <input  size="10"  type="hidden" name="paso30"   value="" id="paso30"   READONLY >
		<input  size="10"  type="hidden" name="paso40"   value="" id="paso40"   READONLY >
		<input  size="10"  type="hidden" name="paso50"   value="" id="paso50"   READONLY >
	  <input  size="10"  type="hidden" name="pitbi"   value="" id="pitbi"   READONLY >
	  <input  size="5"  type="hidden" name="impuesto"   value="" id="impuesto"   READONLY >
	  <input  size="5"  type="hidden" name="impuestoley"   value="" id="impuestoley"   READONLY >
	 <input  size="5" style="font-size:14px" type="text" name="preciom"  placeholder="Precio Minimo" value="" id="preciom"   READONLY >
 	  <input  size="3" style="font-size:14px"  type="text" name="existencia"  placeholder="Existencia" value="" id="existencia"   READONLY >

    <button class="btn btn-md btn-info procesar-pedido butt" id="deli" name="deli" value="" type="button" onclick="deli1();" >
					<span class="glyphicon glyphicon glyphicon glyphicon-search"></span>&nbsp;&nbsp;DELIVERY
					</button>


 <input class="info" style="font-size:12px"  name="clientesep" type="text"  id ="clientesep" value="" size="10" >
&nbsp; Neto RD$:&nbsp
   <input style="font-size:28px"  size="6" type="text" name="neto"   value="0.00" id="neto"   READONLY > </th>
</tr>     
</table>

 <script language="JavaScript">
   document.getElementById('montorec').value= "<?php echo $montorec; ?>";
  document.getElementById('nomrec').value= "<?php echo $nomrec; ?>";
document.getElementById('direc').value= "<?php echo $direc; ?>";
document.getElementById('telrec').value= "<?php echo $telrec; ?>";
 document.getElementById('clientesep').value= "<?php echo $clientesep; ?>";
  document.getElementById('ley').value= "<?php echo $ley; ?>";
    document.getElementById('tp20').value= "<?php echo $tp20; ?>";
	    document.getElementById('pley').value= "<?php echo $pley; ?>"; 
 document.getElementById('tpedido').value= "<?php echo $tpedido; ?>";
   document.getElementById('foto').value= "<?php echo $foto; ?>";
    document.getElementById('grupo').value= "<?php echo $grupo; ?>";
document.getElementById('orden').value= "<?php echo $orden; ?>";
document.getElementById('ordeng').value= "<?php echo $ordeng; ?>";
document.getElementById('cliente').value= "<?php echo $cliente; ?>";
document.getElementById('nombre').value= "<?php echo $nombre; ?>";
document.getElementById('direccion').value= "<?php echo $direccion; ?>";
document.getElementById('telefono').value= "<?php echo $telefono; ?>";
document.getElementById('printerp').value= "<?php echo $printer; ?>";
document.getElementById('condicion').value= "<?php echo $condicion; ?>";
document.getElementById('vendedor').value= "<?php echo $vendedorp2; ?>";
document.getElementById('ncf').value= "<?php echo $ncf; ?>";
document.getElementById('ncf1').value= "<?php echo $ncf1; ?>";
document.getElementById('articulo').value= "<?php echo $articulo; ?>";
document.getElementById('descripcion').value= "<?php echo $descripcion; ?>";
document.getElementById('unidad').value= "<?php echo $unidad; ?>";
document.getElementById('cantidad').value= "<?php echo $cantidad; ?>";
document.getElementById('precio').value= "<?php echo $precio; ?>";
document.getElementById('comentario').value= "<?php echo $comentario; ?>";
document.getElementById('tgeneral').value= "<?php echo $tgeneral; ?>";
document.getElementById('tdescuento').value= "<?php echo $tdescuento; ?>";
document.getElementById('titbi').value= "<?php echo $titbi; ?>";
document.getElementById('importe2').value= "<?php echo $importe2; ?>";
document.getElementById('efectivo').value= "<?php echo $efectivo; ?>";
document.getElementById('devuelta').value= "<?php echo $devuelta; ?>";
document.getElementById('cheque').value= "<?php echo $cheque; ?>";
document.getElementById('tarjeta').value= "<?php echo $tarjeta; ?>";
document.getElementById('neto').value= "<?php echo $neto; ?>";
document.getElementById('paso').value= "<?php echo $paso; ?>";
document.getElementById('pitbi').value= "<?php echo $pitbi; ?>";
document.getElementById('desci').value= "<?php echo $desci; ?>";
document.getElementById('descp').value= "<?php echo $descp; ?>";
document.getElementById('descd').value= "<?php echo $descd; ?>";
document.getElementById('impuesto').value= "<?php echo $impuesto; ?>";
document.getElementById('impuestoley').value= "<?php echo $impuestoley; ?>";
document.getElementById('preciom').value= "<?php echo $preciom; ?>";
document.getElementById('existencia').value= "<?php echo $existencia; ?>";
document.getElementById('rnc').value= "<?php echo $rnc; ?>";
document.getElementById('nomenvio').value= "<?php echo $nomenvio; ?>";
document.getElementById('direnvio').value= "<?php echo $direnvio; ?>";
document.getElementById('telenvio').value= "<?php echo $telenvio; ?>";
document.getElementById('envio1').value= "<?php echo $envio1; ?>";
document.getElementById('itbiincluido').value= "<?php echo $itbiincluido; ?>";
</script>  
   <?php if($articulo=="" ){?>
  <script language="JavaScript">
   document.getElementById('articulo').focus(); <?php } ?>
  	  </script>  
	   <?php if($articulo!="" and $articulo!="*" ){?>
  <script language="JavaScript">
   document.getElementById('cantidad').focus(); <?php } ?>
  	  </script>  
	   <?php if($articulo!="" and $cantidad!=""){?>
  <script language="JavaScript">
   document.getElementById('precio').focus(); <?php } ?>
  	  </script>  

</form>
 </footer>
 </div> 
<!-- Bootstrap -->
<script src="js/bootstrap.js"></script>
<!-- app -->
<script src="js/app.js"></script>
<script src="js/app.plugin.js"></script>
<script src="js/app.data.js"></script>
</body>

