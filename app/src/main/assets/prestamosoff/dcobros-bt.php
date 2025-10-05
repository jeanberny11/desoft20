<?php session_start();

include "include/db.php";


header('Access-Control-Allow-Origin: *');
header("Access-Control-Allow-Headers: X-API-KEY, Origin, X-Requested-With, Content-Type, Accept, Access-Control-Request-Method");
header("Access-Control-Allow-Methods: GET, POST, OPTIONS, PUT");
header("Allow: GET, POST, OPTIONS, PUT");

$datas  = array();


$sql = "SELECT
    *
FROM
    dat_dcob where fec_cob >= DATE_SUB( NOW( ) , INTERVAL 10 DAY )  ";




$datas= buscar_db($sql);



echo json_encode($datas);


?>