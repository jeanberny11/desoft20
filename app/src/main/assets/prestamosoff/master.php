<?php session_start();

include "include/db.php";


header('Access-Control-Allow-Origin: *');
header("Access-Control-Allow-Headers: X-API-KEY, Origin, X-Requested-With, Content-Type, Accept, Access-Control-Request-Method");
header("Access-Control-Allow-Methods: GET, POST, OPTIONS, PUT");
header("Allow: GET, POST, OPTIONS, PUT");

$datas  = array();

$sql = mysqli_query($link, "SELECT
			*
		FROM
			dat_sys
		WHERE cod_sys = '1' ");
//$sqlr = mysql_fetch_assoc($sql);
while($sqlr = mysqli_fetch_assoc($sql)){
	$datas[]=$sqlr;
}




echo json_encode($datas);

?>
