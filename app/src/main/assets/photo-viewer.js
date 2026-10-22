(function(){
'use strict';
const style=document.createElement('style');
style.textContent='#photoViewer{position:fixed;inset:0;z-index:10000;background:rgba(0,0,0,.96);display:none;align-items:center;justify-content:center;overflow:hidden;touch-action:none}#photoViewer.open{display:flex}#photoViewer img{max-width:100vw;max-height:100dvh;object-fit:contain;transform-origin:center center;user-select:none;-webkit-user-drag:none;touch-action:none}#photoViewerClose{position:absolute;right:16px;top:calc(16px + env(safe-area-inset-top));z-index:2;background:#30383e;color:white;border-radius:50%;width:44px;height:44px;padding:0;font-size:26px}img.photo{cursor:zoom-in}';
document.head.appendChild(style);
const overlay=document.createElement('div');overlay.id='photoViewer';overlay.setAttribute('role','dialog');overlay.setAttribute('aria-label','Photo viewer');
const photo=document.createElement('img');photo.alt='Enlarged photo';photo.draggable=false;
const close=document.createElement('button');close.id='photoViewerClose';close.type='button';close.textContent='×';close.setAttribute('aria-label','Close photo');
overlay.append(photo,close);document.body.appendChild(overlay);
let scale=1,x=0,y=0,pointers=new Map(),startDistance=0,startScale=1,lastX=0,lastY=0;
function draw(){photo.style.transform='translate('+x+'px,'+y+'px) scale('+scale+')'}
function reset(){scale=1;x=0;y=0;pointers.clear();draw()}
function open(src){if(!src)return;photo.src=src;reset();overlay.classList.add('open');close.focus()}
function hide(){overlay.classList.remove('open');photo.removeAttribute('src');reset()}
window.PhotoViewer={open,close:hide,isOpen:()=>overlay.classList.contains('open')};
document.addEventListener('click',e=>{const img=e.target.closest&&e.target.closest('img.photo');if(img&&img.src){e.preventDefault();e.stopPropagation();open(img.src)}});
close.addEventListener('click',hide);
overlay.addEventListener('click',e=>{if(e.target===overlay)hide()});
document.addEventListener('keydown',e=>{if(e.key==='Escape'&&window.PhotoViewer.isOpen()){e.preventDefault();hide()}});
overlay.addEventListener('pointerdown',e=>{if(e.target===close)return;e.preventDefault();overlay.setPointerCapture(e.pointerId);pointers.set(e.pointerId,{x:e.clientX,y:e.clientY});if(pointers.size===1){lastX=e.clientX;lastY=e.clientY}else if(pointers.size===2){const a=[...pointers.values()];startDistance=Math.hypot(a[0].x-a[1].x,a[0].y-a[1].y);startScale=scale}});
overlay.addEventListener('pointermove',e=>{if(!pointers.has(e.pointerId))return;e.preventDefault();pointers.set(e.pointerId,{x:e.clientX,y:e.clientY});if(pointers.size===2){const a=[...pointers.values()];scale=Math.min(5,Math.max(1,startScale*Math.hypot(a[0].x-a[1].x,a[0].y-a[1].y)/Math.max(1,startDistance)));if(scale===1){x=0;y=0}}else if(pointers.size===1&&scale>1){x+=e.clientX-lastX;y+=e.clientY-lastY;lastX=e.clientX;lastY=e.clientY}draw()});
function release(e){pointers.delete(e.pointerId);if(pointers.size===1){const a=[...pointers.values()][0];lastX=a.x;lastY=a.y}if(!pointers.size&&scale===1){x=0;y=0;draw()}}
overlay.addEventListener('pointerup',release);overlay.addEventListener('pointercancel',release);
overlay.addEventListener('wheel',e=>{e.preventDefault();scale=Math.min(5,Math.max(1,scale*(e.deltaY<0?1.15:.87)));if(scale===1){x=0;y=0}draw()},{passive:false});
})();