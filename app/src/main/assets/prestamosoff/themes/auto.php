<?php include('functions.php'); 
if(!isset($_SESSION)){session_start();} 
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
"http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Ticket</title>
 <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1" />
  <link rel="stylesheet" href="css/bootstrap.css" type="text/css" />
  <link rel="stylesheet" href="css/font-awesome.min.css" type="text/css" />
  <link rel="stylesheet" href="css/font.css" type="text/css" cache="false" />
  <link rel="stylesheet" href="css/app.css" type="text/css" />
  <script src="js/jquery1.8.3.js"></script>


<script type="text/javascript">
     
function epedido1(){
    document.auto.paso14.value = 'S';
    document.auto.submit() 
   }
function tmesa1(){
    var nt = prompt("Dijite No. Mesa a Transferir:");
	if (nt!=null){
	alert(nt)
 	  document.auto.paso13.value = nt;
	  document.auto.submit() 
     } }

function vcta1(){
    var nt = prompt("Dijite No. Mesa a Consultar:");
	if (nt!=null){
	alert(nt)
 	  document.auto.paso12.value = nt;
	  document.auto.submit() 
     } }

function buscar01(){
    valor = document.getElementById("busq01").value;
if( valor !="BUSQUEDA" ) {
  document.auto.submit() 
} }
function buscar02(){
  valor = document.getElementById("meca").value;
if( valor !="BUSQUEDA" ) {
  document.auto.submit() 
} }


function paso01(){
    document.auto.paso10.value = 'S';
    document.auto.submit() 
   }
function paso02(){
    document.auto.paso11.value = 'S';
     document.auto.submit() 
   }
 
   function salir250(){
      document.auto.paso11.value = 'SALIR';
      document.auto.submit() 
   }
function servicio1(){
      document.auto.paso11.value = 'SERVICIOS';
      document.auto.submit() 
   }
function producto1(){
      document.auto.paso11.value = 'PRODUCTOS';
      document.auto.submit() 
   }
  
function ot1(){
      document.auto.paso11.value = 'OTROS';
      document.auto.submit() 
   }
           
   

   function mesa001(){document.auto.mesav.value = '1';document.pedido2.submit()}
   function mesa002(){document.auto.mesav.value = '2';document.pedido2.submit()}
   function mesa003(){document.auto.mesav.value = '3';document.auto.submit()}
   function mesa004(){document.auto.mesav.value = '4';document.auto.submit()}
   function mesa005(){document.auto.mesav.value = '5';document.auto.submit()}
   function mesa006(){document.auto.mesav.value = '6';document.auto.submit()}
   function mesa007(){document.auto.mesav.value = '7';document.auto.submit()}
   function mesa008(){document.auto.mesav.value = '8';document.auto.submit()}
   function mesa009(){document.auto.mesav.value = '9';document.auto.submit()}
   function mesa0010(){document.auto.mesav.value = '10';document.auto.submit()}
  

</script>
<style type="text/css">
#grande{ /*border:1px solid #FE2E2E;*/
         width:100%;
         height: auto;       
	}
#uno{ border:3px solid #467BD7; 
      width:840px;  height:1200px;
      display:inline-block;   
	  
	    
	}
#dos{ border:3px solid #467BD7; 
      width:840px; height:600px;
      display:inline-block;
	}
#tres{ border:3px solid #467BD7;
       width:270px; height:600px;
       display:inline-block;
	}
#cuatro{ border:3px solid #467BD7;
	width:270px; height:600px;
       display:inline-block;
	} 
	
	#cinco{ border:3px solid #467BD7;
	  width:60%;; height:300px;
       display:inline-block;
	} 
	      
</style>


</head>
<body>

<form class="form horizontal" id="auto" name="auto" method="post" action="auto.php"  >	
    <div id="grande">
    <div id="uno">
	  <table class="table table-striped table-bordered table-hover table-condensed " >
  	
 <tr class="info">
   <th colspan="5"> <b> 
    No.Fosa:
  <input class="info" style="font-size:18px" name="codigo" type="text"  id ="codigo" value="" size="1" />
 
 	 
   <select name="busq01" size="1" value ="BUSQUEDA" id="busq01" onclick="buscar01()" >  
   <option value="BUSQUEDA">BUSQUEDA</option>
 <?php 
 $centralg =$_SESSION['central'];
 $estafeta=  $_SESSION['estafeta'];

$buscar1 = mysqli_query($link, "select * from  mesa where  estafeta='$estafeta' AND central='$centralg' ORDER BY codigo ASC  ");
while ($row = mysqli_fetch_assoc($buscar1)){
 ?> <option value="<?php  echo $row['codigo'].'='.substr($row['nombre'],0,20); ?>"><?php  echo  $row['codigo'].'='.substr($row['nombre'],0,20); ?></option> <?php 
}
?>
 </select>
 	
    <b><FONT SIZE=4><?php echo substr($_SESSION['usuario'],0,10); ?></FONT>
  	
   <input class="btn btn-sm btn-success"  type="submit" name="procesar" value="Ejecutar" id = "procesar" onClick="paso01()" />
   <input class="btn btn-sm btn-primary"  type="button" name="orden" value="Ordenes" id = "orden" onClick="paso02()" />
   <input class="btn btn-sm btn-primary"  type="button" name="vcta" value="V.Cta." id = "vcta" onClick="vcta1()" />
   <input class="btn btn-sm btn-primary"  type="button" name="tmesa" value="Cambio Fosa" id = "tmesa" onClick="tmesa1()" />
   <input class="btn btn-sm btn-success"  type="button" name="salir25" value="Salir" id = "salir25" onClick="salir250()" />
  
 <?php   
$fotop= $_SESSION['fondo1'];  if(trim($fotop)!=""){  ?>
 <img border border="2" src="<?php echo $_SESSION['fondo1']; ?>" width="100"  height="30" title="Soluciones Tecnologica LOTECOM.NET" />
  <?php }?> </th>
</tr>
</table>



 <!-- invisible  Pantalla 01   <div id="pantalla01" style="visibility:hidden;"> -->
 
   <table class="table table-striped table-bordered table-hover table-condensed " >
   
   	 <tr class="info">
   <th colspan="5">  <b>  
No.Placa:
  <input class="info" style="font-size:14px"  name="placa" type="text"  id ="placa" value="" size="10" />
Cliente:
 <input style="font-size:14px"  size="10" type="text" name="cliente" placeholder="Cliente"  value="" id="cliente">
 <input class="info" style="font-size:14px" placeholder="Nombre" name="nombre" type="text" id ="nombre" value="" size="35" /> 
 </tr>
 <tr class="info">
<th colspan="5">   <b> 
  
<input class="info" style="font-size:14px" placeholder="Vehiculo" name="vehiculo" type="text"  id ="vehiculo" value="" size="25" />
<input style="font-size:14px"  size="22" type="text" placeholder="Modelo" name="modelo"   value="" id="modelo">
<input class="info" style="font-size:14px" placeholder="Transmision" name="transmision" type="text" id ="transmision" value="" size="20"/><input class="info" style="font-size:14px" placeholder="Kilometraje" name="kilometraje" type="text" id ="kilometraje" value="" size="8" /> 

 </tr>


	 <tr class="info">
   <th colspan="5">  <b>  
  <input class="info" style="font-size:14px"  name="articulo" type="text"  id ="articulo" value="" size="5" />
  <input class="info" style="font-size:14px"  name="descripcion" type="text"  id ="descripcion" value="" size="17" READONLY /> 
 <input class="info" style="font-size:14px" placeholder="Comentario" name="comentario" type="text" id ="comentario" value="" size="20" /> 
  <input class="info" style="font-size:14px"  name="cantidad" type="text"  id ="cantidad" value="" size="6" />
  <input class="info" style="font-size:16px"  name="precio" type="text"  id ="precio" value="" size="4" />
  	<input class="info" style="font-size:16px"  name="importep" type="text"  id ="importep" value="" size="4" READONLY/></th>
</tr>
 <tr class="info">
   <th colspan="5"> <b>   
  
   <input style="font-size:14px"  size="23" type="text" name="bnombre" placeholder="Buscar"  value="" id="bnombre">
   
   Mecanico:
 <input style="font-size:14px"  size="10" type="text" name="mecanico"  value="" id="mecanico">
   <select name="meca" size="1" value ="BUSQUEDA" id="meca" onclick="busca02()" > 
       <option value="BUSQUEDA">BUSQUEDA</option>
     <?php 
$centralg =  $_SESSION['central'];
$estafeta =  $_SESSION['estafeta'];
$buscar1 = mysqli_query($link, "select * from  vendedor where estafeta='$estafeta' and central='$centralg' ORDER BY codigo ASC ");
while ($row = mysqli_fetch_assoc($buscar1)){
 ?> <option value="<?php  echo $row['codigo'].'='.substr($row['nombre'],0,8); ?>"><?php  echo  $row['codigo'].'='.substr($row['nombre'],0,8); ?></option> <?php 
}

?>
 </select>
   
   
      <button class="btn btn-md btn-danger procesar-pedido butt btn-lg " id="epedido"  onClick="epedido1()" >
			<span class="glyphicon glyphicon glyphicon glyphicon-search"></span>&nbsp;&nbsp;ENV.PEDIDO
		</button>


			
						</th>
</tr>

<tr class="info">
   <th colspan="5">  

   <button class="btn btn-md btn-danger procesar-pedido butt" id="servicio"  onClick="servicio1()" >
			<span class="glyphicon glyphicon glyphicon glyphicon-search"></span>&nbsp;&nbspSERVICIOS</button>
       
     <button class="btn btn-md btn-success procesar-pedido butt" id="producto"  onClick="producto1()" >
						<span class="glyphicon glyphicon glyphicon glyphicon-search"></span>&nbsp;&nbspPRODUCTOS</button>

   	<button class="btn btn-md btn-info procesar-pedido butt" id="otros"  onClick="ot1()" >
		<span class="glyphicon glyphicon glyphicon glyphicon-search"></span>&nbsp;&nbsp;OTROS					</button>	</th>
</tr>
 <input  size="10"  type="hidden" name="clavepp2"   value="" id="clavepp2"   READONLY >
<input  size="10"  type="hidden" name="secuencia"   value="" id="secuencia"   READONLY >
 <input  size="10"  type="hidden" name="grupo"   value="" id="grupo"   READONLY >
 <input  size="10"  type="hidden" name="paso10"   value="" id="paso10"   READONLY >
  <input  size="10"  type="hidden" name="paso11"   value="" id="paso11"   READONLY >
    <input  size="10"  type="hidden" name="paso12"   value="" id="paso12"   READONLY >
	 <input  size="10"  type="hidden" name="paso13"   value="" id="paso13"   READONLY >
	 <input  size="10"  type="hidden" name="paso14"   value="" id="paso14"   READONLY >
	  <input  size="10"  type="hidden" name="mesav"   value="" id="mesav"   READONLY >
 <input  size="60"  type="hidden" name="foto"   value="" id="foto"   READONLY >
</table>

<!-- Fin Pantalla 01  </div> -->

 <?php
$centralg =$_SESSION['central'];
$estafeta =$_SESSION['estafeta'];
$fecha =$_SESSION['fecha'];
$usuariop =$_SESSION['nombre'];
$secu =$_SESSION['hora'];$hora =$_SESSION['hora'];
if(trim($centralg)==""){echo '<script language="javascript">alert("Atencion... Esta Fuera de  SECCION. Debe Reinicial el Sistema........");</script>';header('Location: http://168.144.159.40/facturacom/index.php');}
//Buscar Codigo para Actualizar
if(isset($_POST['codigo'])) {$codigo =$_POST['codigo'];$codigo =strtoupper(trim($codigo));} else {$codgio ="";}
if(isset($_POST['secuencia'])) {$secuencia =$_POST['secuencia'];$secuenica =strtoupper(trim($secuencia));} else {$secuencia ="";}
if(isset($_POST['cliente'])) {$cliente =$_POST['cliente'];$cliente =strtoupper(trim($cliente));} else {$cliente ="";}
if(isset($_POST['cantidad'])) { $cantidad =$_POST['cantidad']; $cantidad =strtoupper(trim($cantidad));$paso="S";} else {$cantidad ="";$paso="N";}
if(isset($_POST['paso10'])) {$paso10 =$_POST['paso10'];} 
if(isset($_POST['paso11'])) {$paso11 =$_POST['paso11'];} else {$paso11="N";}
if(isset($_POST['paso12'])) {$paso12 =$_POST['paso12'];} else {$paso12="";}
if(isset($_POST['paso13'])) {$paso13 =$_POST['paso13'];} else {$paso13="";}
if(isset($_POST['paso14'])) {$paso14 =$_POST['paso14'];} else {$paso14="";}
if(isset($_POST['mecanico'])) {$mecanico =$_POST['mecanico'];$mecanico =strtoupper(trim($mecancio));  } else {$mecanico="";}
if(isset($_POST['meca'])) {$valor1 = $_POST['meca'];$meca=$_POST['meca'];$valor2=split("[=]",$valor1);$mecanico=$valor2[0]; } else {$meca='BUSQUEDA';}
if(isset($_POST['vehiculo'])) {$vehiculo =$_POST['vehiculo'];$vehiculo =strtoupper(trim($vehiculo));} else {$vehiculo="";}
if(isset($_POST['modelo'])) {$modelo =$_POST['modelo'];$cantidad =strtoupper(trim($modelo));} else {$modelo="";} 
if(isset($_POST['nombre'])) {$nombre =$_POST['nombre'];$nombre =strtoupper(trim($nombre));} else {$nombre="";}
if(isset($_POST['transmision'])) {$transmision =$_POST['transmision'];$transmision =strtoupper(trim($transmision));} else {$transmison="";}if(isset($_POST['kilometraje'])) {$kilometraje =$_POST['kilometraje'];$kilometraje =strtoupper(trim($kilometraje));} else {$kilometraje="";}
if(isset($_POST['placa'])) {$placa =$_POST['placa'];$placa =strtoupper(trim($placa));} else {$placa="";} 

if(isset($_POST['articulo'])) {$articulo =$_POST['articulo'];$articulo =strtoupper(trim($articulo));} else {$articulo ="";}
if(isset($_POST['foto'])) {$foto =$_POST['foto'];} else {$foto ="";}
if(isset($_POST['grupo'])) {$grupo =$_POST['grupo'];} else {$grupo ="";}
if(isset($_GET['clavepp2'])) {$clavepp2 =$_GET['clavepp2'];$clavepp2 =strtoupper(trim($clavepp2));} else {$clavepp2="";}
if(isset($_POST['precio'])) {$precio =$_POST['precio'];$precio =strtoupper(trim($precio));} else {$precio ="";}
if(isset($_POST['descripcion'])) {$descripcion =$_POST['descripcion'];$descripcion =strtoupper(trim($descripcion));} else {$descripcion ="";}
if(isset($_POST['bnombre'])) {$bnombre =$_POST['bnombre'];$bnombre =strtoupper(trim($bnombre));} else {$bnombre ="";}
if(isset($_POST['comentario'])) { $comentario =$_POST['comentario'];$comentario =strtoupper(trim($comentario));} else {$comentario ="";}
if(isset($_POST['busq01'])) {$valor1 = $_POST['busq01'];$valor2=split("[=]",$valor1); $valor3=$valor2[0];
if($valor3!="BUSQUEDA"){$codigo=$valor2[0]; $codigo =strtoupper(trim($codigo));  ?><script language="JavaScript">document.getElementById('codigo').value= "<?php echo $codigo; ?>";	</script><?php  }} 

if(isset($_GET['visualizar1'])) {$valor1 = $_GET['visualizar1'];$valor2=split("[=]",$valor1);$articulo=$valor2[0];$codigo=$valor2[1];
 ?>  <script language="JavaScript">   document.getElementById('codigo').value= "<?php echo $codigo; ?>"; 
 document.getElementById('articulo').value= "<?php echo $articulo; ?>"; 
  	</script>    <?php 
} 
if(isset($_POST['importep'])) {$importep =$_POST['importep'];} else {$importep="";}
if(isset($_GET['visualizar2'])) {$valor1 = $_GET['visualizar2'];$valor2=split("[=]",$valor1);$articulo=$valor2[0];$codigo=$valor2[1];
 ?>  <script language="JavaScript">   document.getElementById('codigo').value= "<?php echo $codigo; ?>"; 
 document.getElementById('articulo').value= "<?php echo $articulo; ?>";  document.auto.submit() 
  	</script>    <?php } 

//Borrar Codigo
if(isset($_GET['borrar'])) {$valor1 = $_GET['borrar'];$valor2=split("[=]",$valor1);$cod=$valor2[0];$codigo=$valor2[1];
$placa=$valor2[2];$cliente=$valor2[3];$nombre=$valor2[4];$vehiculo=$valor2[5];$modelo=$valor2[6];$transmision=$valor2[7];$kilometraje=$valor2[8]; $articulo="";  $borrar =  mysqli_query($link, " DELETE FROM temporal WHERE  secuencia='$cod' and estafeta ='$estafeta' and  central='$centralg' "); } 

if(isset($_GET['comp4'])) { $valor1 = $_GET['comp4']; $valor2=split("[=]",$valor1); $secuencia=$valor2[0]; $codigo=$valor2[1];
$placa=$valor2[2];$cliente=$valor2[3];$nombre=$valor2[4];$vehiculo=$valor2[5];$modelo=$valor2[6];$transmision=$valor2[7];$kilometraje=$valor2[8]; $descripcion="";  ?>  <script language="JavaScript">   document.getElementById('codigo').value= "<?php echo $codigo; ?>"; document.getElementById('secuencia').value= "<?php echo $secuencia; ?>"; document.getElementById('comentario').focus();	</script>    <?php 
} 

if(isset($_GET['clp4'])) { $valor1 = $_GET['clp4']; $valor2=split("[=]",$valor1); $secuencia=$valor2[0]; $codigo=$valor2[1]; $descripcion=""; $placa=$valor2[2];$cliente=$valor2[3];$nombre=$valor2[4];$vehiculo=$valor2[5];$modelo=$valor2[6];$transmision=$valor2[7];$kilometraje=$valor2[8]; ?>  <script language="JavaScript">   document.getElementById('codigo').value= "<?php echo $codigo; ?>"; document.getElementById('secuencia').value= "<?php echo $secuencia; ?>"; document.getElementById('mecanico').focus();	</script>    <?php 
} 


if(isset($_GET['masp'])) { $valor1 = $_GET['masp']; $valor2=split("[=]",$valor1); $secuencia=$valor2[0]; $codigo=$valor2[1];$placa=$valor2[2];$cliente=$valor2[3];$nombre=$valor2[4];$vehiculo=$valor2[5];$modelo=$valor2[6];$transmision=$valor2[7];$kilometraje=$valor2[8];
 $inicio = mysqli_query($link, "select * from temporal where  secuencia='$secuencia' and estafeta ='$estafeta' and central='$centralg' ");
	$inicio1 = mysqli_num_rows($inicio);
	if ($inicio1 > 0 ){ $inicio2 = mysqli_fetch_array($inicio);	
     $cantidad = $inicio2['cantidad']+1; 
	  $pp12 = mysqli_query($link, "UPDATE temporal set cantidad='$cantidad' where secuencia='$secuencia' and estafeta ='$estafeta' and  central='$centralg' ");  } $articulo=""; $cantidad=""; $secuencia="";} 



if(isset($_GET['menop'])) { $valor1 = $_GET['menop']; $valor2=split("[=]",$valor1); $secuencia=$valor2[0]; $codigo=$valor2[1];$placa=$valor2[2];$cliente=$valor2[3];$nombre=$valor2[4];$vehiculo=$valor2[5];$modelo=$valor2[6];$transmision=$valor2[7];$kilometraje=$valor2[8];
 $inicio = mysqli_query($link, "select * from temporal where  secuencia='$secuencia' and estafeta ='$estafeta' and central='$centralg' ");
	$inicio1 = mysqli_num_rows($inicio);
	if ($inicio1 > 0 ){ $inicio2 = mysqli_fetch_array($inicio);	$cantidad = $inicio2['cantidad'];
     if ($inicio2['cantidad'] > 1 ){$cantidad = $inicio2['cantidad']-1;}
	  
	  $pp12 = mysqli_query($link, "UPDATE temporal set cantidad='$cantidad' where secuencia='$secuencia' and estafeta ='$estafeta' and  central='$centralg' ");  } $articulo=""; $cantidad=""; $secuencia="";} 



//Buscar placa

//F// modificar codigo
if ($placa!="" and  $vehiculo=="" ){
$inicio = mysqli_query($link, "select * from transporte where placa='$placa' AND estafeta='$estafeta' AND central='$centralg' ");
	$inicio1 = mysqli_num_rows($inicio);
	if ($inicio1 > 0 ){ $inicio2 = mysqli_fetch_array($inicio);	
         $cliente = $inicio2['cliente']; $nombre = $inicio2['empresa']; $modelo = $inicio2['modelo'];
		 $vehiculo = $inicio2['camion'];$kilometraje = $inicio2['kilometraje'];$transmision = $inicio2['transmision'];
  }}
//Fiin Buscar Placa



//Borrar Codigo
if(isset($_GET['visu01'])) { $valor1 = $_GET['visu01']; $valor2=split("[=]",$valor1); $secuencia=$valor2[0]; $codigo=$valor2[1]; $descripcion="";  ?>  <script language="JavaScript">   document.getElementById('codigo').value= "<?php echo $codigo; ?>"; document.getElementById('secuencia').value= "<?php echo $secuencia; ?>"; 	</script>    <?php 
} 

if (trim($paso11)=="SALIR"){  echo "<script type=\"text/javascript\">location.href=\"menu.php\";</script>"; }

if (trim($paso11)=="SERVICIOS"){$bnombre="SERVICIOS";if (trim($codigo)==""){echo '<script language="javascript">alert("Atencion... Debe Digitar  No.FOSA.");</script>';$bnombre="";}}

if (trim($paso11)=="PRODUCTOS"){$bnombre="PRODUCTOS";if (trim($codigo)==""){echo '<script language="javascript">alert("Atencion... Debe Digitar  No.FOSA");</script>';$bnombre="";}}

if (trim($paso11)=="OTROS"){$bnombre="OTROS";if (trim($codigo)==""){echo '<script language="javascript">alert("Atencion... Debe Digitar  No. FOSA");</script>';$bnombre="";}}


//Procesar con un Botton
if (trim($paso14)!="" ){
if(trim($codigo)==""){$paso14="";echo '<script language="javascript">alert("Atencion...  Mesa en Blanco..");</script>';$codigo="";}
if($importep > 1 ){$articulo="P";} else {$paso14="";echo '<script language="javascript">alert("Atencion...  No Tiene Registro en la Orden..");</script>';$codigo="";}   }
// fin Boton procesa



 
//buscar Cuadro Mesa 
if(isset($_POST['mesav'])) {$mesav=$_POST['mesav'];if(trim($mesav)!=""){$codigo =$_POST['mesav'];}}
 ?> <script language="JavaScript"> document.getElementById('codigo').value= "<?php echo $codigo; ?>"; </script>   <?php 
if(trim($codigo)=="") {$mesav="";
echo'<div  style="overflow: auto; width: 67%; border:3px solid #467BD7; display:inline-block; ">';
 $buscar1 = mysqli_query($link, "select * from mesa where  estafeta ='$estafeta' and  central='$centralg' ORDER BY nombre ASC  ");
$fila = mysqli_num_rows($buscar1);

while ($row = mysqli_fetch_assoc($buscar1)){
$est=$row['estado'];
if(trim($est)=="Libre"){$xx="btn btn-md btn-info procesar-pedido butt btn-lg "; } else {$xx="btn btn-md btn-danger procesar-pedido butt  btn-lg";}
	$nombre=$row['nombre'];$x=$row['codigo'];
	  echo '<button class="'.$xx.'" id="mesav1" name="mesav1" onClick="mesa00'.$x.'()" >'.$nombre.'</button>&nbsp;';	 } 
echo'</div>';}
//Fin cuadro mesa




if(isset($_GET['visualizar3'])) {$valor1 = $_GET['visualizar3'];$valor2=split("[=]",$valor1);$secuencia=$valor2[0];$codigo=$valor2[1];
 ?>  <script language="JavaScript">   document.getElementById('codigo').value= "<?php echo $codigo; ?>"; 
 document.getElementById('articulo').value= "<?php echo $articulo; ?>"; 
  	</script>    <?php 
//Salida de Producto
$pp11 = mysqli_query($link, "UPDATE pedido set estado2='ENTREGADO' where central='$centralg' and estafeta='$estafeta' AND secuencia='$secuencia'");} 

// Buscar el Mesa
if ($codigo!=""  ){
$inicio = mysqli_query($link, "select * from mesa where codigo='$codigo' AND estafeta='$estafeta' AND central='$centralg' ");
$inicio1 = mysqli_num_rows($inicio);
if ($inicio1 > 0 ){ 
$inicio1 = mysqli_fetch_array($inicio);	
	     $usuariopp = $inicio1['camarero'];
	      if(trim($usuariopp)==""){$usuariopp=$usuariop;}
	 if ( trim($usuariopp)!=trim($usuariop)){ echo '<script language="javascript">alert("Atencion... Esta Mesa esta Abierta Esta Ocpuada po Otro Camarero(a)...");</script>';$codigo=""; }
		 } else {
	 	 echo '<script language="javascript">alert("Atencion... Mesa No Registra..");</script>';
	 $articulo="";$descripcion="";$precio="";$cantidad="";$paso="N";$comentario="";$codigo="";
	 }	}
//fin buscar mesa




//Salvar Pedido
if ($articulo=="P" and $codigo!=""){
 $buscar1 = mysqli_query($link, "select * from pedido where posteo ='Normal' and mesa ='$codigo' and estafeta ='$estafeta' and  central='$centralg' AND  usuario='$usuariop' ORDER BY secuencia DESC  ");
while ($row = mysqli_fetch_assoc($buscar1)){
 $secuencia=$row['secuencia'];
 $pp11 = mysqli_query($link, "UPDATE pedido set posteo='Pedido',estado='Pedido',estado2='Pedido' where central='$centralg' and mesa='$codigo' AND estafeta='$estafeta' and secuencia='$secuencia' "); } 
$pp11 = mysqli_query($link, "UPDATE mesa set estado='Ocupada',camarero='$usuariop' where codigo='$codigo' AND estafeta='$estafeta' AND central='$centralg' "); $articulo="";$descripcion="";$precio="";$cantidad="";$paso="N";$comentario="";$secuencia="";$codigo="";	
echo '<script language="javascript">alert("Atencion... Orden Enviada    E X I T O S A M E N T E.......");  </script>';$codigo="";	}
//Fin Salvar Pedido




// Articulo Existencte en el pedido y lo modificoo
if ($codigo!="" and  $secuencia!=""  and  $descripcion=="" ){
$inicio = mysqli_query($link, "select * from pedido where mesa='$codigo' AND estafeta='$estafeta' AND central='$centralg' AND secuencia='$secuencia' ");
	$inicio1 = mysqli_num_rows($inicio);
	if ($inicio1 > 0 ){ $inicio2 = mysqli_fetch_array($inicio);	
	$cliente = $inicio2['cliente']; $articulo = $inicio2['articulo'];$precio = $inicio2['precio'];$descripcion = $inicio2['descripcion'];$comentario = $inicio2['comentario'];$precio = $inicio2['precio']; $cantidad = $inicio2['cantidad'];$paso="N"; }}
      //asignar los valores de la db a los campos vacios


 
		
	// Buscar el Articulo
if ($articulo!=""  and  $descripcion=="" ){
$inicio = mysqli_query($link, "select * from articulos where codigo='$articulo' AND estafeta='$estafeta' AND central='$centralg' ");
$inicio1 = mysqli_num_rows($inicio);
if ($inicio1 > 0 ){ $inicio2 = mysqli_fetch_array($inicio);	
    $descripcion = $inicio2['descripcion']; $foto = $inicio2['foto']; $grupo = $inicio2['grupo'];
	 $precio = $inicio2['precio1']; $precio = bcadd($precio,'0',2); 
	 if($precio >1){$cantidad="1";}
	 
	 }else {
	 	 echo '<script language="javascript">alert("Atencion... Articulo No  Registrado..");</script>';
	 $articulo="";$descripcion="";$precio="";$cantidad="";$paso="N";$comentario="";
	 }	}



   // Agregart el Item Factura
if($articulo!="" and $cantidad!="" and $precio!="" and $paso=="S" ) {

$tg = $precio * $cantidad;$importe2= $tg;$tgeneral2=$tg;
$secuencia=$centralg.$estafeta.$usuariop.$fecha.substr($secu,0,2).substr($secu,3,2).substr($secu,6,2);
$pp3 = mysqli_query($link, "INSERT INTO temporal(central,estafeta,usuario,articulo,descripcion,unidad,cantidad,precio,comentario,costo,secuencia,fecha,tgeneral,importe,foto,mecanico,fosa) values ('$centralg','$estafeta','$usuariop','$articulo','$descripcion','UNIDAD','$cantidad','$precio','$comentario','$costo','$secuencia','$fecha','0.00' ,'0.00','$foto','$mecanico','$codigo'  )");
$articulo="";$descripcion="";$precio="";$cantidad="";$paso="N";$comentario="";$secuencia="";$grupo="";$cliente="";
}


		?>	
	 <script language="JavaScript"> 
	    document.getElementById('mecanico').value= "<?php echo $mecanico; ?>";
	  document.getElementById('vehiculo').value= "<?php echo $vehiculo; ?>";
	  document.getElementById('placa').value= "<?php echo $placa; ?>";
	  document.getElementById('modelo').value= "<?php echo $modelo; ?>";
	  document.getElementById('nombre').value= "<?php echo $nombre; ?>";
	  document.getElementById('kilometraje').value= "<?php echo $kilometraje; ?>";
	  document.getElementById('transmision').value= "<?php echo $transmision; ?>";
      document.getElementById('importep').value= "<?php echo $importep; ?>";
	  document.getElementById('cliente').value= "<?php echo $cliente; ?>";
	  document.getElementById('codigo').value= "<?php echo $codigo; ?>";
	  document.getElementById('articulo').value= "<?php echo $articulo; ?>";
	  document.getElementById('precio').value= "<?php echo $precio; ?>";
	  document.getElementById('descripcion').value= "<?php echo $descripcion; ?>";
	  document.getElementById('cantidad').value= "<?php echo $cantidad; ?>";
	  document.getElementById('comentario').value= "<?php echo $comentario; ?>";
	  document.getElementById('grupo').value= "<?php echo $grupo; ?>";
	  document.getElementById('secuencia').value= "<?php echo $secuencia; ?>";
	  document.getElementById('foto').value= "<?php echo $foto; ?>";
	</script> 
	
	  <?php
	  
	  
	  //Boton de Ordenes o FAMILIA
if ( trim($paso11)=="S"){   ?>
<div id="dos">	<div style="overflow: auto; width: 75%; height:600px;">
<table class="table table-bordered table-hover table-condensed" style="font-size:18px"  >
  <tr class="info">
    <th width="5" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">No.Mesa</span></th>
    <th width="100" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Servicios</span></th>
	<th width="5" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Cant.</span></th>
 	<th width="5" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Cond.</span></th>
  </tr>
  <?php 
$centralg =$_SESSION['central'];$estafeta =$_SESSION['estafeta'];$tot=0; $bat=0;
$buscar1 = mysqli_query($link, "select * from  pedido where usuario ='$usuariop' and estado2='SALIDA' and  estafeta='$estafeta' and central='$centralg' ORDER BY mesa ASC "); while ($row = mysqli_fetch_assoc($buscar1)){
      $cod=$row['secuencia'].'='.$codigo; $m=$row['mesa'];$d=$row['articulo'].'-'.$row['descripcion'];
      $fotop = $row['foto'];	  $xx="";
	  echo '<b><tr class="'.$xx.'" class="active" >';
	  echo '<td width="10"><b>'.'<a href="auto.php?visualizar3='.$cod.'" title="'.$m.'">'.$m.'</a>'.'</td>';
	  echo '<td width="10"><b>'.'<a href="auto.php?visualizar3='.$cod.'" title="'.$d.'">'.$d.'</a>'.'</td>';
	  echo '<td><b>'.$row['cantidad'].'</td>';
	  $fotop = '../inventario/images/bien.PNG';
      echo '<td><img border border="2" src="'.$fotop.'" width="50" height="25"/></td>';
     echo'</tr></b>';
    }  echo '</table> </div> 	</div>';  	}
	//fin Buscar Ordenes O FAMILIA
	
	  
	  
	  
	//vISUALIZA ITEN PEDIDO  
 if(trim($codigo)!="" and trim($bnombre)=="" and trim($paso12)=="" ){?>	
<div style="overflow: auto; width: 75%; height:550px;">
<table class="table table-striped table-bordered table-hover"  style="font-size:14px" >
  <tr class="info">
    <th width="20" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Cliente</span></th>
   <th width="50" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Articulo</span></th>
    <th width="3" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">cant.</span></th>
	<th width="3" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Precio</span></th>
	<th width="3" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Mas</span></th>
	<th width="3" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Meno</span></th>
	<th width="3" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Com</span></th>
	<th width="3" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Cta.S</span></th>
	<th width="3" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Borrar</span></th>
    </tr>	
    </tr>
  <?php 
	 

$tgeneral =0.00;$tdescuento =0.00;$titbi =0.00;$neto =0.00;$importep =0.00;
//Listaa articulos registrado factura
 $buscar1 = mysqli_query($link, "select * from temporal where  fosa ='$codigo' and estafeta ='$estafeta' and  central='$centralg' ORDER BY secuencia DESC  ");
while ($row = mysqli_fetch_assoc($buscar1)){
$borrarp=$row['secuencia'].'='.$codigo.'='.$placa.'='.$cliente.'='.$nombre.'='.$vehiculo.'='.$modelo.'='.$transmision.'='.$kilometraje;
$importep=$importep+($row['cantidad']*$row['precio']);
$bn=$row['articulo'].'-'.$row['descripcion'].' '.$row['comentario'];$clip=$row['mecanico'];

		$xx="";
		echo '<b><tr class="'.$xx.'" class="active" >';
		echo '<td><b>'.'<a href="auto.php?visu01='.$borrarp.'" title="V.Item">'.$clip.'</a>'.'</td>';
     	echo '<td><b>'.'<a href="auto.php?visu01='.$borrarp.'" title="V.Item">'.$bn.'</a>'.'</td>';
	    echo '<td><b>'.trim($row['cantidad']).'</td>';
		echo '<td><b>'.number_format($row['precio'],2).'</td>';
		 echo'<td><b>'.'<a href="auto.php?masp='.$borrarp.'" ><img src="../rifamovil/images/agre.png" width="35"  height="20" title="  Agregar Mas Cantidad" /></a>'.'</td>';

 echo'<td><b>'.'<a href="auto.php?menop='.$borrarp.'" ><img src="../facturacom/images/meno2.png" width="30"  height="20" title="  Meno a la Cantidad" /></a>'.'</td>';

	    echo'<td><b>'.'<a href="auto.php?comp4='.$borrarp.'" ><img src="../rifamovil/images/edit.png" width="35"  height="20" title="  Comentario de Item" /></a>'.'</td>';
	
			 echo'<td><b>'.'<a href="auto.php?clp4='.$borrarp.'" ><img src="../rifamovil/images/editar.jpg" width="35"  height="20" title="Cliente Cta. Separada" /></a>'.'</td>';
	
	 echo'<td><b>'.'<a href="auto.php?borrar='.$borrarp.'" ><img src="../facturacom/images/borrar.png" width="25"  height="15" title="  Borrar Item o Pedido" /></a>'.'</td>';
	
	
   	   echo'</tr>';
	 } }

 $importep = bcadd($importep,'0',2);
   ?>
  </table>
	
	 <?php  if (trim($bnombre)!=""){  ?>
   <div id="dos">
	<div style="overflow: auto; width: 75%; height:600px;">
<table class="table table-bordered table-hover table-condensed" style="font-size:18px"  >
  <tr class="info">
   <th width="100" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Servicios</span></th>
   <th width="50" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6">Precio</span></th>
    <th width="50" bordercolor="#00FF66" bgcolor="#33FF99"><span class="Estilo6"></span></th>
  </tr>
  <?php 
  if (trim($bnombre)=="OTROS"){$bnombre="";}
$centralg =$_SESSION['central'];
$estafeta =$_SESSION['estafeta'];
 $buscar1 = mysqli_query($link, "select * from articulos where estafeta ='$estafeta' and  central='$centralg' AND  CONCAT(codigo,'',descripcion,'',unidad,'',precio1,'',ubicacion)  LIKE '%".$bnombre."%'  ");
	$d="";	 
while ($row = mysqli_fetch_assoc($buscar1)){
  	   	$posteo  = $row['posteo'];
		$tt=$tt+1;
        $mont += $row['balance'];
		 $secpp=$row['codigo'].'='.$orden.'='.$ncf.'='.$ncf1.'='.$condicion.'='.$vendedorp2.'='.$camarero;
		$xx="";
	  $cod=$row['codigo'].'='.$codigo;$fotop="";$d=$row['codigo'].'-'.substr($row['descripcion'],0,25);$p=$row['precio1'];
      $fotop = $row['foto'];
	  echo '<b><tr class="'.$xx.'" class="active" >';
	   echo '<td width="10"><b>'.'<a href="auto.php?visualizar2='.$cod.'" title="Elegir">'.$d.'</a>'.'</td>';
	     echo '<td width="10"><b>'.'<a href="auto.php?visualizar2='.$cod.'" title="Elegir">'.$p.'</a>'.'</td>';
	  	  if(trim($fotop)!=""){echo '<td width="5"><b><img border border="2" src="'.$fotop.'" width="45" height="30"/></td>';} else{echo '<td width="5"></td>'; }
   	   echo'</tr>';
   	}
     echo '</table> </div> 	</div>';
} 

	?>
	
    </div>
		
	
	 <script language="JavaScript"> 
	     document.getElementById('mecanico').value= "<?php echo $mecanico; ?>";
	 document.getElementById('importep').value= "<?php echo $importep; ?>";
	  		document.getElementById('cliente').value= "<?php echo $cliente; ?>";
	  document.getElementById('codigo').value= "<?php echo $codigo; ?>";
	  document.getElementById('precio').value= "<?php echo $precio; ?>";
	  document.getElementById('descripcion').value= "<?php echo $descripcion; ?>";
	  document.getElementById('cantidad').value= "<?php echo $cantidad; ?>";
	  document.getElementById('articulo').value= "<?php echo $articulo; ?>";
	  document.getElementById('comentario').value= "<?php echo $comentario; ?>";
	  document.getElementById('secuencia').value= "<?php echo $secuencia; ?>";
	  	</script> 
	
		   <?php if($codigo==""){?>
  <script language="JavaScript">
   document.getElementById('codigo').focus(); <?php } ?>
  	  </script>  
	
	  <?php if($codigo!="" and $articulo==""){?>
  <script language="JavaScript">
   document.getElementById('articulo').focus(); <?php } ?>
  	  </script>
	  
	   <?php if($codigo!="" and $articulo!="" and $cantidad==""  ){?>
  <script language="JavaScript">
   document.getElementById('cantidad').focus(); <?php } ?>
  	  </script>  
	    
	
	
	
</form>
</body>
<!-- Bootstrap -->
<script src="js/bootstrap.js"></script>
<!-- app -->
<script src="js/app.js"></script>
<script src="js/app.plugin.js"></script>
<script src="js/app.data.js"></script>

 

</html>