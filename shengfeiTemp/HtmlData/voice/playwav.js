var audio;
var fn;
$(function(){
if ("undefined" == typeof h5){h5=0;}
if ((navigator.userAgent.indexOf('MSIE') >= 0) 
    && (navigator.userAgent.indexOf('Opera') < 0)){
   h5=0;
}
if (navigator.userAgent.indexOf('Chrome') >= 0){
   h5=1;
}
if (navigator.userAgent.indexOf('Safari') >= 0){
   h5=1;
}
$("div[f]").each(function(i){
atxt="<a href=\"javascript:void(0);\" title=\"音频\" onclick=\"playmp3('"+$(this).attr("f")+"')\""+"><img border='0' src='voice\\play.png'></a>";
$(this).html(atxt);
});
$("div[v]").each(function(i){
atxt="<a href=\"javascript:void(0);\" title=\"视频\" onclick=\"playmp4('"+$(this).attr("v")+"')\""+"><img border='0' src='voice\\mp4.png'></a>";
$(this).html(atxt);
});
audio = document.getElementById('audio');
if(audio==null){
$(document.body).append("<div id='mp3play' style='display:none'><audio src='' controls='controls'  id='audio' width='0' height='0'><object id='soundControl' classid='CLSID:6BF52A52-394A-11d3-B153-00C04F79FAA6' height='0' width='0'><param name='URL' value='' /></object></div>");
}

audio = document.getElementById('audio');
mp4player = document.getElementById('mp4player');
if(mp4player==null){
$(document.body).append("<div id='mp4player' class='fixed-box'><a href='javascript:void(0)'>X</a><video src='' autobuffer='' controls='controls' id='h5player' width='320' height='240'></video><embed id='h4player' src='' autostart='0' width='320' height='240' /></div>");
$(document.body).append("<link href='voice/css.css' rel='stylesheet' style='text/css' />");

}
//if(h5==1){$('#video').appendTo($('#maskdiv'));}//else{$('#mp4player').appendTo($('#maskdiv'));}
$("#mp4player a").click(function(){$("#mp4player").hide();});
$("#mp4player").hide();

});//////////////////////////////////////////////////////////////////////////$
function playmp3(fn_new){
if(h5==1){		//h5
if(fn!=fn_new){audio.src="voice/"+fn_new+".wav";
fn=fn_new;}
if(audio.paused){
audio.play();
return;
}
audio.pause();
}
else{			//html
audio = document.getElementById('soundControl');
//alert(audio.src);
if(fn!=fn_new){

audio.url="voice/"+fn_new+".wav";
audio.controls.play();
fn=fn_new;
}
else{
audio.controls.stop();
fn="";
}
}
}

function playmp4(fn_new){
if(h5==1){		//h5
$("#h5player").attr("src","voice/"+fn_new+".mp4");
$("#mp4player").show();
$("#h5player").show();
}
else
{
$("#mp4player").html("<div id='mp4player'><a href='javascript:void(0)' onclick='$(\"#mp4player\").hide();'>X</a><embed id='h4player' src='"+fn_new+".mp4' autostart='0' width='320' height='240' /></div>");
//$("#h4player").html("<div>123</div>");
$("#h4player").attr("src","voice/"+fn_new+".mp4");
$("#mp4player").show();
$("#h4player").show();
}//if
}

$(function(){
$(".showtitle").click(function()
{
    $(this).siblings().toggle();
});
});