import{n as G,aa as lt,z as ft,s as ut,I as dt,F as ht}from"./scheduler.DKCQ0og6.js";new URL("sveltekit-internal://");function pt(e,n){return e==="/"||n==="ignore"?e:n==="never"?e.endsWith("/")?e.slice(0,-1):e:n==="always"&&!e.endsWith("/")?e+"/":e}function gt(e){return e.split("%25").map(decodeURI).join("%25")}function mt(e){for(const n in e)e[n]=decodeURIComponent(e[n]);return e}function ue({href:e}){return e.split("#")[0]}const _t=["href","pathname","search","toString","toJSON"];function yt(e,n,t){const a=new URL(e);Object.defineProperty(a,"searchParams",{value:new Proxy(a.searchParams,{get(r,o){if(o==="get"||o==="getAll"||o==="has")return s=>(t(s),r[o](s));n();const i=Reflect.get(r,o);return typeof i=="function"?i.bind(r):i}}),enumerable:!0,configurable:!0});for(const r of _t)Object.defineProperty(a,r,{get(){return n(),e[r]},enumerable:!0,configurable:!0});return a}const wt="/__data.json",vt=".html__data.json";function bt(e){return e.endsWith(".html")?e.replace(/\.html$/,vt):e.replace(/\/$/,"")+wt}function Et(...e){let n=5381;for(const t of e)if(typeof t=="string"){let a=t.length;for(;a;)n=n*33^t.charCodeAt(--a)}else if(ArrayBuffer.isView(t)){const a=new Uint8Array(t.buffer,t.byteOffset,t.byteLength);let r=a.length;for(;r;)n=n*33^a[--r]}else throw new TypeError("value must be a string or TypedArray");return(n>>>0).toString(36)}function kt(e){const n=atob(e),t=new Uint8Array(n.length);for(let a=0;a<n.length;a++)t[a]=n.charCodeAt(a);return t.buffer}const Fe=window.fetch;window.fetch=(e,n)=>((e instanceof Request?e.method:(n==null?void 0:n.method)||"GET")!=="GET"&&M.delete(_e(e)),Fe(e,n));const M=new Map;function St(e,n){const t=_e(e,n),a=document.querySelector(t);if(a!=null&&a.textContent){let{body:r,...o}=JSON.parse(a.textContent);const i=a.getAttribute("data-ttl");return i&&M.set(t,{body:r,init:o,ttl:1e3*Number(i)}),a.getAttribute("data-b64")!==null&&(r=kt(r)),Promise.resolve(new Response(r,o))}return window.fetch(e,n)}function At(e,n,t){if(M.size>0){const a=_e(e,t),r=M.get(a);if(r){if(performance.now()<r.ttl&&["default","force-cache","only-if-cached",void 0].includes(t==null?void 0:t.cache))return new Response(r.body,r.init);M.delete(a)}}return window.fetch(n,t)}function _e(e,n){let a=`script[data-sveltekit-fetched][data-url=${JSON.stringify(e instanceof Request?e.url:e)}]`;if(n!=null&&n.headers||n!=null&&n.body){const r=[];n.headers&&r.push([...new Headers(n.headers)].join(",")),n.body&&(typeof n.body=="string"||ArrayBuffer.isView(n.body))&&r.push(n.body),a+=`[data-hash="${Et(...r)}"]`}return a}const Rt=/^(\[)?(\.\.\.)?(\w+)(?:=(\w+))?(\])?$/;function It(e){const n=[];return{pattern:e==="/"?/^\/$/:new RegExp(`^${Pt(e).map(a=>{const r=/^\[\.\.\.(\w+)(?:=(\w+))?\]$/.exec(a);if(r)return n.push({name:r[1],matcher:r[2],optional:!1,rest:!0,chained:!0}),"(?:/(.*))?";const o=/^\[\[(\w+)(?:=(\w+))?\]\]$/.exec(a);if(o)return n.push({name:o[1],matcher:o[2],optional:!0,rest:!1,chained:!0}),"(?:/([^/]+))?";if(!a)return;const i=a.split(/\[(.+?)\](?!\])/);return"/"+i.map((c,l)=>{if(l%2){if(c.startsWith("x+"))return de(String.fromCharCode(parseInt(c.slice(2),16)));if(c.startsWith("u+"))return de(String.fromCharCode(...c.slice(2).split("-").map(f=>parseInt(f,16))));const u=Rt.exec(c),[,h,p,d,g]=u;return n.push({name:d,matcher:g,optional:!!h,rest:!!p,chained:p?l===1&&i[0]==="":!1}),p?"(.*?)":h?"([^/]*)?":"([^/]+?)"}return de(c)}).join("")}).join("")}/?$`),params:n}}function Lt(e){return!/^\([^)]+\)$/.test(e)}function Pt(e){return e.slice(1).split("/").filter(Lt)}function xt(e,n,t){const a={},r=e.slice(1),o=r.filter(s=>s!==void 0);let i=0;for(let s=0;s<n.length;s+=1){const c=n[s];let l=r[s-i];if(c.chained&&c.rest&&i&&(l=r.slice(s-i,s+1).filter(u=>u).join("/"),i=0),l===void 0){c.rest&&(a[c.name]="");continue}if(!c.matcher||t[c.matcher](l)){a[c.name]=l;const u=n[s+1],h=r[s+1];u&&!u.rest&&u.optional&&h&&c.chained&&(i=0),!u&&!h&&Object.keys(a).length===o.length&&(i=0);continue}if(c.optional&&c.chained){i++;continue}return}if(!i)return a}function de(e){return e.normalize().replace(/[[\]]/g,"\\$&").replace(/%/g,"%25").replace(/\//g,"%2[Ff]").replace(/\?/g,"%3[Ff]").replace(/#/g,"%23").replace(/[.*+?^${}()|\\]/g,"\\$&")}function Tt({nodes:e,server_loads:n,dictionary:t,matchers:a}){const r=new Set(n);return Object.entries(t).map(([s,[c,l,u]])=>{const{pattern:h,params:p}=It(s),d={id:s,exec:g=>{const f=h.exec(g);if(f)return xt(f,p,a)},errors:[1,...u||[]].map(g=>e[g]),layouts:[0,...l||[]].map(i),leaf:o(c)};return d.errors.length=d.layouts.length=Math.max(d.errors.length,d.layouts.length),d});function o(s){const c=s<0;return c&&(s=~s),[c,e[s]]}function i(s){return s===void 0?s:[r.has(s),e[s]]}}function Ve(e,n=JSON.parse){try{return n(sessionStorage[e])}catch{}}function Pe(e,n,t=JSON.stringify){const a=t(n);try{sessionStorage[e]=a}catch{}}const O=[];function Ut(e,n){return{subscribe:re(e,n).subscribe}}function re(e,n=G){let t;const a=new Set;function r(s){if(ut(e,s)&&(e=s,t)){const c=!O.length;for(const l of a)l[1](),O.push(l,e);if(c){for(let l=0;l<O.length;l+=2)O[l][0](O[l+1]);O.length=0}}}function o(s){r(s(e))}function i(s,c=G){const l=[s,c];return a.add(l),a.size===1&&(t=n(r,o)||G),s(e),()=>{a.delete(l),a.size===0&&t&&(t(),t=null)}}return{set:r,update:o,subscribe:i}}function cn(e,n,t){const a=!Array.isArray(e),r=a?[e]:e;if(!r.every(Boolean))throw new Error("derived() expects stores as input, got a falsy value");const o=n.length<2;return Ut(t,(i,s)=>{let c=!1;const l=[];let u=0,h=G;const p=()=>{if(u)return;h();const g=n(a?l[0]:l,i,s);o?i(g):h=dt(g)?g:G},d=r.map((g,f)=>lt(g,_=>{l[f]=_,u&=~(1<<f),c&&p()},()=>{u|=1<<f}));return c=!0,p(),function(){ft(d),h(),c=!1}})}var De;const P=((De=globalThis.__sveltekit_m8e7px)==null?void 0:De.base)??"";var Ce;const Nt=((Ce=globalThis.__sveltekit_m8e7px)==null?void 0:Ce.assets)??P,Ot="1716050569860",qe="sveltekit:snapshot",Ge="sveltekit:scroll",Me="sveltekit:states",jt="sveltekit:pageurl",D="sveltekit:history",H="sveltekit:navigation",W={tap:1,hover:2,viewport:3,eager:4,off:-1,false:-1},Y=location.origin;function Be(e){if(e instanceof URL)return e;let n=document.baseURI;if(!n){const t=document.getElementsByTagName("base");n=t.length?t[0].href:document.URL}return new URL(e,n)}function ye(){return{x:pageXOffset,y:pageYOffset}}function j(e,n){return e.getAttribute(`data-sveltekit-${n}`)}const xe={...W,"":W.hover};function He(e){let n=e.assignedSlot??e.parentNode;return(n==null?void 0:n.nodeType)===11&&(n=n.host),n}function ze(e,n){for(;e&&e!==n;){if(e.nodeName.toUpperCase()==="A"&&e.hasAttribute("href"))return e;e=He(e)}}function pe(e,n){let t;try{t=new URL(e instanceof SVGAElement?e.href.baseVal:e.href,document.baseURI)}catch{}const a=e instanceof SVGAElement?e.target.baseVal:e.target,r=!t||!!a||oe(t,n)||(e.getAttribute("rel")||"").split(/\s+/).includes("external"),o=(t==null?void 0:t.origin)===Y&&e.hasAttribute("download");return{url:t,external:r,target:a,download:o}}function X(e){let n=null,t=null,a=null,r=null,o=null,i=null,s=e;for(;s&&s!==document.documentElement;)a===null&&(a=j(s,"preload-code")),r===null&&(r=j(s,"preload-data")),n===null&&(n=j(s,"keepfocus")),t===null&&(t=j(s,"noscroll")),o===null&&(o=j(s,"reload")),i===null&&(i=j(s,"replacestate")),s=He(s);function c(l){switch(l){case"":case"true":return!0;case"off":case"false":return!1;default:return}}return{preload_code:xe[a??"off"],preload_data:xe[r??"off"],keepfocus:c(n),noscroll:c(t),reload:c(o),replace_state:c(i)}}function Te(e){const n=re(e);let t=!0;function a(){t=!0,n.update(i=>i)}function r(i){t=!1,n.set(i)}function o(i){let s;return n.subscribe(c=>{(s===void 0||t&&c!==s)&&i(s=c)})}return{notify:a,set:r,subscribe:o}}function $t(){const{set:e,subscribe:n}=re(!1);let t;async function a(){clearTimeout(t);try{const r=await fetch(`${Nt}/_app/version.json`,{headers:{pragma:"no-cache","cache-control":"no-cache"}});if(!r.ok)return!1;const i=(await r.json()).version!==Ot;return i&&(e(!0),clearTimeout(t)),i}catch{return!1}}return{subscribe:n,check:a}}function oe(e,n){return e.origin!==Y||!e.pathname.startsWith(n)}const Dt=-1,Ct=-2,Ft=-3,Vt=-4,qt=-5,Gt=-6;function Mt(e,n){if(typeof e=="number")return r(e,!0);if(!Array.isArray(e)||e.length===0)throw new Error("Invalid input");const t=e,a=Array(t.length);function r(o,i=!1){if(o===Dt)return;if(o===Ft)return NaN;if(o===Vt)return 1/0;if(o===qt)return-1/0;if(o===Gt)return-0;if(i)throw new Error("Invalid input");if(o in a)return a[o];const s=t[o];if(!s||typeof s!="object")a[o]=s;else if(Array.isArray(s))if(typeof s[0]=="string"){const c=s[0],l=n==null?void 0:n[c];if(l)return a[o]=l(r(s[1]));switch(c){case"Date":a[o]=new Date(s[1]);break;case"Set":const u=new Set;a[o]=u;for(let d=1;d<s.length;d+=1)u.add(r(s[d]));break;case"Map":const h=new Map;a[o]=h;for(let d=1;d<s.length;d+=2)h.set(r(s[d]),r(s[d+1]));break;case"RegExp":a[o]=new RegExp(s[1],s[2]);break;case"Object":a[o]=Object(s[1]);break;case"BigInt":a[o]=BigInt(s[1]);break;case"null":const p=Object.create(null);a[o]=p;for(let d=1;d<s.length;d+=2)p[s[d]]=r(s[d+1]);break;default:throw new Error(`Unknown type ${c}`)}}else{const c=new Array(s.length);a[o]=c;for(let l=0;l<s.length;l+=1){const u=s[l];u!==Ct&&(c[l]=r(u))}}else{const c={};a[o]=c;for(const l in s){const u=s[l];c[l]=r(u)}}return a[o]}return r(0)}const Ke=new Set(["load","prerender","csr","ssr","trailingSlash","config"]);[...Ke];const Bt=new Set([...Ke]);[...Bt];function Ht(e){return e.filter(n=>n!=null)}class se{constructor(n,t){this.status=n,typeof t=="string"?this.body={message:t}:t?this.body=t:this.body={message:`Error: ${n}`}}toString(){return JSON.stringify(this.body)}}class Ye{constructor(n,t){this.status=n,this.location=t}}class we extends Error{constructor(n,t,a){super(a),this.status=n,this.text=t}}const zt="x-sveltekit-invalidated",Kt="x-sveltekit-trailing-slash";function Z(e){return e instanceof se||e instanceof we?e.status:500}function Yt(e){return e instanceof we?e.text:"Internal Error"}const N=Ve(Ge)??{},z=Ve(qe)??{},T={url:Te({}),page:Te({}),navigating:re(null),updated:$t()};function ve(e){N[e]=ye()}function Jt(e,n){let t=e+1;for(;N[t];)delete N[t],t+=1;for(t=n+1;z[t];)delete z[t],t+=1}function F(e){return location.href=e.href,new Promise(()=>{})}function Ue(){}let ie,ge,Q,x,me,V;const Je=[],ee=[];let R=null;const We=[],Wt=[];let $=[],y={branch:[],error:null,url:null},be=!1,te=!1,Ne=!0,K=!1,q=!1,Xe=!1,Ee=!1,ke,S,L,I,ne;const B=new Set;async function ln(e,n,t){var r,o;document.URL!==location.href&&(location.href=location.href),V=e,ie=Tt(e),x=document.documentElement,me=n,ge=e.nodes[0],Q=e.nodes[1],ge(),Q(),S=(r=history.state)==null?void 0:r[D],L=(o=history.state)==null?void 0:o[H],S||(S=L=Date.now(),history.replaceState({...history.state,[D]:S,[H]:L},""));const a=N[S];a&&(history.scrollRestoration="manual",scrollTo(a.x,a.y)),t?await rn(me,t):nn(location.href,{replaceState:!0}),an()}function Xt(){Je.length=0,Ee=!1}function Ze(e){ee.some(n=>n==null?void 0:n.snapshot)&&(z[e]=ee.map(n=>{var t;return(t=n==null?void 0:n.snapshot)==null?void 0:t.capture()}))}function Qe(e){var n;(n=z[e])==null||n.forEach((t,a)=>{var r,o;(o=(r=ee[a])==null?void 0:r.snapshot)==null||o.restore(t)})}function Oe(){ve(S),Pe(Ge,N),Ze(L),Pe(qe,z)}async function et(e,n,t,a){return J({type:"goto",url:Be(e),keepfocus:n.keepFocus,noscroll:n.noScroll,replace_state:n.replaceState,state:n.state,redirect_count:t,nav_token:a,accept:()=>{n.invalidateAll&&(Ee=!0)}})}async function Zt(e){if(e.id!==(R==null?void 0:R.id)){const n={};B.add(n),R={id:e.id,token:n,promise:nt({...e,preload:n}).then(t=>(B.delete(n),t.type==="loaded"&&t.state.error&&(R=null),t))}}return R.promise}async function he(e){const n=ie.find(t=>t.exec(at(e)));n&&await Promise.all([...n.layouts,n.leaf].map(t=>t==null?void 0:t[1]()))}function tt(e,n,t){var o;y=e.state;const a=document.querySelector("style[data-sveltekit]");a&&a.remove(),I=e.props.page,ke=new V.root({target:n,props:{...e.props,stores:T,components:ee},hydrate:t}),Qe(L);const r={from:null,to:{params:y.params,route:{id:((o=y.route)==null?void 0:o.id)??null},url:new URL(location.href)},willUnload:!1,type:"enter",complete:Promise.resolve()};$.forEach(i=>i(r)),te=!0}async function ae({url:e,params:n,branch:t,status:a,error:r,route:o,form:i}){let s="never";if(P&&(e.pathname===P||e.pathname===P+"/"))s="always";else for(const d of t)(d==null?void 0:d.slash)!==void 0&&(s=d.slash);e.pathname=pt(e.pathname,s),e.search=e.search;const c={type:"loaded",state:{url:e,params:n,branch:t,error:r,route:o},props:{constructors:Ht(t).map(d=>d.node.component),page:I}};i!==void 0&&(c.props.form=i);let l={},u=!I,h=0;for(let d=0;d<Math.max(t.length,y.branch.length);d+=1){const g=t[d],f=y.branch[d];(g==null?void 0:g.data)!==(f==null?void 0:f.data)&&(u=!0),g&&(l={...l,...g.data},u&&(c.props[`data_${h}`]=l),h+=1)}return(!y.url||e.href!==y.url.href||y.error!==r||i!==void 0&&i!==I.form||u)&&(c.props.page={error:r,params:n,route:{id:(o==null?void 0:o.id)??null},state:{},status:a,url:new URL(e),form:i??null,data:u?l:I.data}),c}async function Se({loader:e,parent:n,url:t,params:a,route:r,server_data_node:o}){var u,h,p;let i=null,s=!0;const c={dependencies:new Set,params:new Set,parent:!1,route:!1,url:!1,search_params:new Set},l=await e();if((u=l.universal)!=null&&u.load){let d=function(...f){for(const _ of f){const{href:b}=new URL(_,t);c.dependencies.add(b)}};const g={route:new Proxy(r,{get:(f,_)=>(s&&(c.route=!0),f[_])}),params:new Proxy(a,{get:(f,_)=>(s&&c.params.add(_),f[_])}),data:(o==null?void 0:o.data)??null,url:yt(t,()=>{s&&(c.url=!0)},f=>{s&&c.search_params.add(f)}),async fetch(f,_){let b;f instanceof Request?(b=f.url,_={body:f.method==="GET"||f.method==="HEAD"?void 0:await f.blob(),cache:f.cache,credentials:f.credentials,headers:f.headers,integrity:f.integrity,keepalive:f.keepalive,method:f.method,mode:f.mode,redirect:f.redirect,referrer:f.referrer,referrerPolicy:f.referrerPolicy,signal:f.signal,..._}):b=f;const A=new URL(b,t);return s&&d(A.href),A.origin===t.origin&&(b=A.href.slice(t.origin.length)),te?At(b,A.href,_):St(b,_)},setHeaders:()=>{},depends:d,parent(){return s&&(c.parent=!0),n()},untrack(f){s=!1;try{return f()}finally{s=!0}}};i=await l.universal.load.call(null,g)??null}return{node:l,loader:e,server:o,universal:(h=l.universal)!=null&&h.load?{type:"data",data:i,uses:c}:null,data:i??(o==null?void 0:o.data)??null,slash:((p=l.universal)==null?void 0:p.trailingSlash)??(o==null?void 0:o.slash)}}function je(e,n,t,a,r,o){if(Ee)return!0;if(!r)return!1;if(r.parent&&e||r.route&&n||r.url&&t)return!0;for(const i of r.search_params)if(a.has(i))return!0;for(const i of r.params)if(o[i]!==y.params[i])return!0;for(const i of r.dependencies)if(Je.some(s=>s(new URL(i))))return!0;return!1}function Ae(e,n){return(e==null?void 0:e.type)==="data"?e:(e==null?void 0:e.type)==="skip"?n??null:null}function Qt(e,n){if(!e)return new Set(n.searchParams.keys());const t=new Set([...e.searchParams.keys(),...n.searchParams.keys()]);for(const a of t){const r=e.searchParams.getAll(a),o=n.searchParams.getAll(a);r.every(i=>o.includes(i))&&o.every(i=>r.includes(i))&&t.delete(a)}return t}function $e({error:e,url:n,route:t,params:a}){return{type:"loaded",state:{error:e,url:n,route:t,params:a,branch:[]},props:{page:I,constructors:[]}}}async function nt({id:e,invalidating:n,url:t,params:a,route:r,preload:o}){if((R==null?void 0:R.id)===e)return B.delete(R.token),R.promise;const{errors:i,layouts:s,leaf:c}=r,l=[...s,c];i.forEach(m=>m==null?void 0:m().catch(()=>{})),l.forEach(m=>m==null?void 0:m[1]().catch(()=>{}));let u=null;const h=y.url?e!==y.url.pathname+y.url.search:!1,p=y.route?r.id!==y.route.id:!1,d=Qt(y.url,t);let g=!1;const f=l.map((m,v)=>{var U;const E=y.branch[v],k=!!(m!=null&&m[0])&&((E==null?void 0:E.loader)!==m[1]||je(g,p,h,d,(U=E.server)==null?void 0:U.uses,a));return k&&(g=!0),k});if(f.some(Boolean)){try{u=await st(t,f)}catch(m){const v=await C(m,{url:t,params:a,route:{id:e}});return B.has(o)?$e({error:v,url:t,params:a,route:r}):ce({status:Z(m),error:v,url:t,route:r})}if(u.type==="redirect")return u}const _=u==null?void 0:u.nodes;let b=!1;const A=l.map(async(m,v)=>{var le;if(!m)return;const E=y.branch[v],k=_==null?void 0:_[v];if((!k||k.type==="skip")&&m[1]===(E==null?void 0:E.loader)&&!je(b,p,h,d,(le=E.universal)==null?void 0:le.uses,a))return E;if(b=!0,(k==null?void 0:k.type)==="error")throw k;return Se({loader:m[1],url:t,params:a,route:r,parent:async()=>{var Le;const Ie={};for(let fe=0;fe<v;fe+=1)Object.assign(Ie,(Le=await A[fe])==null?void 0:Le.data);return Ie},server_data_node:Ae(k===void 0&&m[0]?{type:"skip"}:k??null,m[0]?E==null?void 0:E.server:void 0)})});for(const m of A)m.catch(()=>{});const w=[];for(let m=0;m<l.length;m+=1)if(l[m])try{w.push(await A[m])}catch(v){if(v instanceof Ye)return{type:"redirect",location:v.location};if(B.has(o))return $e({error:await C(v,{params:a,url:t,route:{id:r.id}}),url:t,params:a,route:r});let E=Z(v),k;if(_!=null&&_.includes(v))E=v.status??E,k=v.error;else if(v instanceof se)k=v.body;else{if(await T.updated.check())return await F(t);k=await C(v,{params:a,url:t,route:{id:r.id}})}const U=await en(m,w,i);return U?await ae({url:t,params:a,branch:w.slice(0,U.idx).concat(U.node),status:E,error:k,route:r}):await ot(t,{id:r.id},k,E)}else w.push(void 0);return await ae({url:t,params:a,branch:w,status:200,error:null,route:r,form:n?void 0:null})}async function en(e,n,t){for(;e--;)if(t[e]){let a=e;for(;!n[a];)a-=1;try{return{idx:a+1,node:{node:await t[e](),loader:t[e],data:{},server:null,universal:null}}}catch{continue}}}async function ce({status:e,error:n,url:t,route:a}){const r={};let o=null;if(V.server_loads[0]===0)try{const l=await st(t,[!0]);if(l.type!=="data"||l.nodes[0]&&l.nodes[0].type!=="data")throw 0;o=l.nodes[0]??null}catch{(t.origin!==Y||t.pathname!==location.pathname||be)&&await F(t)}const s=await Se({loader:ge,url:t,params:r,route:a,parent:()=>Promise.resolve({}),server_data_node:Ae(o)}),c={node:await Q(),loader:Q,universal:null,server:null,data:null};return await ae({url:t,params:r,branch:[s,c],status:e,error:n,route:null})}function Re(e,n){if(!e||oe(e,P))return;let t;try{t=V.hooks.reroute({url:new URL(e)})??e.pathname}catch{return}const a=at(t);for(const r of ie){const o=r.exec(a);if(o)return{id:e.pathname+e.search,invalidating:n,route:r,params:mt(o),url:e}}}function at(e){return gt(e.slice(P.length)||"/")}function rt({url:e,type:n,intent:t,delta:a}){let r=!1;const o=ct(y,t,e,n);a!==void 0&&(o.navigation.delta=a);const i={...o.navigation,cancel:()=>{r=!0,o.reject(new Error("navigation cancelled"))}};return K||We.forEach(s=>s(i)),r?null:o}async function J({type:e,url:n,popped:t,keepfocus:a,noscroll:r,replace_state:o,state:i={},redirect_count:s=0,nav_token:c={},accept:l=Ue,block:u=Ue}){const h=Re(n,!1),p=rt({url:n,type:e,delta:t==null?void 0:t.delta,intent:h});if(!p){u();return}const d=S,g=L;l(),K=!0,te&&T.navigating.set(p.navigation),ne=c;let f=h&&await nt(h);if(!f){if(oe(n,P))return await F(n);f=await ot(n,{id:null},await C(new we(404,"Not Found",`Not found: ${n.pathname}`),{url:n,params:{},route:{id:null}}),404)}if(n=(h==null?void 0:h.url)||n,ne!==c)return p.reject(new Error("navigation aborted")),!1;if(f.type==="redirect")if(s>=20)f=await ce({status:500,error:await C(new Error("Redirect loop"),{url:n,params:{},route:{id:null}}),url:n,route:{id:null}});else return et(new URL(f.location,n).href,{},s+1,c),!1;else f.props.page.status>=400&&await T.updated.check()&&await F(n);if(Xt(),ve(d),Ze(g),f.props.page.url.pathname!==n.pathname&&(n.pathname=f.props.page.url.pathname),i=t?t.state:i,!t){const w=o?0:1,m={[D]:S+=w,[H]:L+=w,[Me]:i};(o?history.replaceState:history.pushState).call(history,m,"",n),o||Jt(S,L)}if(R=null,f.props.page.state=i,te){y=f.state,f.props.page&&(f.props.page.url=n);const w=(await Promise.all(Wt.map(m=>m(p.navigation)))).filter(m=>typeof m=="function");if(w.length>0){let m=function(){$=$.filter(v=>!w.includes(v))};w.push(m),$.push(...w)}ke.$set(f.props),Xe=!0}else tt(f,me,!1);const{activeElement:_}=document;await ht();const b=t?t.scroll:r?ye():null;if(Ne){const w=n.hash&&document.getElementById(decodeURIComponent(n.hash.slice(1)));b?scrollTo(b.x,b.y):w?w.scrollIntoView():scrollTo(0,0)}const A=document.activeElement!==_&&document.activeElement!==document.body;!a&&!A&&on(),Ne=!0,f.props.page&&(I=f.props.page),K=!1,e==="popstate"&&Qe(L),p.fulfil(void 0),$.forEach(w=>w(p.navigation)),T.navigating.set(null)}async function ot(e,n,t,a){return e.origin===Y&&e.pathname===location.pathname&&!be?await ce({status:a,error:t,url:e,route:n}):await F(e)}function tn(){let e;x.addEventListener("mousemove",o=>{const i=o.target;clearTimeout(e),e=setTimeout(()=>{a(i,2)},20)});function n(o){a(o.composedPath()[0],1)}x.addEventListener("mousedown",n),x.addEventListener("touchstart",n,{passive:!0});const t=new IntersectionObserver(o=>{for(const i of o)i.isIntersecting&&(he(i.target.href),t.unobserve(i.target))},{threshold:0});function a(o,i){const s=ze(o,x);if(!s)return;const{url:c,external:l,download:u}=pe(s,P);if(l||u)return;const h=X(s);if(!h.reload)if(i<=h.preload_data){const p=Re(c,!1);p&&Zt(p)}else i<=h.preload_code&&he(c.pathname)}function r(){t.disconnect();for(const o of x.querySelectorAll("a")){const{url:i,external:s,download:c}=pe(o,P);if(s||c)continue;const l=X(o);l.reload||(l.preload_code===W.viewport&&t.observe(o),l.preload_code===W.eager&&he(i.pathname))}}$.push(r),r()}function C(e,n){if(e instanceof se)return e.body;const t=Z(e),a=Yt(e);return V.hooks.handleError({error:e,event:n,status:t,message:a})??{message:a}}function nn(e,n={}){return e=Be(e),e.origin!==Y?Promise.reject(new Error("goto: invalid URL")):et(e,n,0)}function an(){var n;history.scrollRestoration="manual",addEventListener("beforeunload",t=>{let a=!1;if(Oe(),!K){const r=ct(y,void 0,null,"leave"),o={...r.navigation,cancel:()=>{a=!0,r.reject(new Error("navigation cancelled"))}};We.forEach(i=>i(o))}a?(t.preventDefault(),t.returnValue=""):history.scrollRestoration="auto"}),addEventListener("visibilitychange",()=>{document.visibilityState==="hidden"&&Oe()}),(n=navigator.connection)!=null&&n.saveData||tn(),x.addEventListener("click",async t=>{var p;if(t.button||t.which!==1||t.metaKey||t.ctrlKey||t.shiftKey||t.altKey||t.defaultPrevented)return;const a=ze(t.composedPath()[0],x);if(!a)return;const{url:r,external:o,target:i,download:s}=pe(a,P);if(!r)return;if(i==="_parent"||i==="_top"){if(window.parent!==window)return}else if(i&&i!=="_self")return;const c=X(a);if(!(a instanceof SVGAElement)&&r.protocol!==location.protocol&&!(r.protocol==="https:"||r.protocol==="http:")||s)return;if(o||c.reload){rt({url:r,type:"link"})?K=!0:t.preventDefault();return}const[u,h]=r.href.split("#");if(h!==void 0&&u===ue(location)){const[,d]=y.url.href.split("#");if(d===h){t.preventDefault(),h===""||h==="top"&&a.ownerDocument.getElementById("top")===null?window.scrollTo({top:0}):(p=a.ownerDocument.getElementById(h))==null||p.scrollIntoView();return}if(q=!0,ve(S),e(r),!c.replace_state)return;q=!1}t.preventDefault(),await new Promise(d=>{requestAnimationFrame(()=>{setTimeout(d,0)}),setTimeout(d,100)}),J({type:"link",url:r,keepfocus:c.keepfocus,noscroll:c.noscroll,replace_state:c.replace_state??r.href===location.href})}),x.addEventListener("submit",t=>{if(t.defaultPrevented)return;const a=HTMLFormElement.prototype.cloneNode.call(t.target),r=t.submitter;if(((r==null?void 0:r.formMethod)||a.method)!=="get")return;const i=new URL((r==null?void 0:r.hasAttribute("formaction"))&&(r==null?void 0:r.formAction)||a.action);if(oe(i,P))return;const s=t.target,c=X(s);if(c.reload)return;t.preventDefault(),t.stopPropagation();const l=new FormData(s),u=r==null?void 0:r.getAttribute("name");u&&l.append(u,(r==null?void 0:r.getAttribute("value"))??""),i.search=new URLSearchParams(l).toString(),J({type:"form",url:i,keepfocus:c.keepfocus,noscroll:c.noscroll,replace_state:c.replace_state??i.href===location.href})}),addEventListener("popstate",async t=>{var a;if((a=t.state)!=null&&a[D]){const r=t.state[D];if(ne={},r===S)return;const o=N[r],i=t.state[Me]??{},s=new URL(t.state[jt]??location.href),c=t.state[H],l=ue(location)===ue(y.url);if(c===L&&(Xe||l)){e(s),N[S]=ye(),o&&scrollTo(o.x,o.y),i!==I.state&&(I={...I,state:i},ke.$set({page:I})),S=r;return}const h=r-S;await J({type:"popstate",url:s,popped:{state:i,scroll:o,delta:h},accept:()=>{S=r,L=c},block:()=>{history.go(-h)},nav_token:ne})}else if(!q){const r=new URL(location.href);e(r)}}),addEventListener("hashchange",()=>{q&&(q=!1,history.replaceState({...history.state,[D]:++S,[H]:L},"",location.href))});for(const t of document.querySelectorAll("link"))t.rel==="icon"&&(t.href=t.href);addEventListener("pageshow",t=>{t.persisted&&T.navigating.set(null)});function e(t){y.url=t,T.page.set({...I,url:t}),T.page.notify()}}async function rn(e,{status:n=200,error:t,node_ids:a,params:r,route:o,data:i,form:s}){be=!0;const c=new URL(location.href);({params:r={},route:o={id:null}}=Re(c,!1)||{});let l;try{const u=a.map(async(d,g)=>{const f=i[g];return f!=null&&f.uses&&(f.uses=it(f.uses)),Se({loader:V.nodes[d],url:c,params:r,route:o,parent:async()=>{const _={};for(let b=0;b<g;b+=1)Object.assign(_,(await u[b]).data);return _},server_data_node:Ae(f)})}),h=await Promise.all(u),p=ie.find(({id:d})=>d===o.id);if(p){const d=p.layouts;for(let g=0;g<d.length;g++)d[g]||h.splice(g,0,void 0)}l=await ae({url:c,params:r,branch:h,status:n,error:t,form:s,route:p??null})}catch(u){if(u instanceof Ye){await F(new URL(u.location,location.href));return}l=await ce({status:Z(u),error:await C(u,{url:c,params:r,route:o}),url:c,route:o})}l.props.page&&(l.props.page.state={}),tt(l,e,!0)}async function st(e,n){var r;const t=new URL(e);t.pathname=bt(e.pathname),e.pathname.endsWith("/")&&t.searchParams.append(Kt,"1"),t.searchParams.append(zt,n.map(o=>o?"1":"0").join(""));const a=await Fe(t.href);if(!a.ok){let o;throw(r=a.headers.get("content-type"))!=null&&r.includes("application/json")?o=await a.json():a.status===404?o="Not Found":a.status===500&&(o="Internal Error"),new se(a.status,o)}return new Promise(async o=>{var h;const i=new Map,s=a.body.getReader(),c=new TextDecoder;function l(p){return Mt(p,{Promise:d=>new Promise((g,f)=>{i.set(d,{fulfil:g,reject:f})})})}let u="";for(;;){const{done:p,value:d}=await s.read();if(p&&!u)break;for(u+=!d&&u?`
`:c.decode(d,{stream:!0});;){const g=u.indexOf(`
`);if(g===-1)break;const f=JSON.parse(u.slice(0,g));if(u=u.slice(g+1),f.type==="redirect")return o(f);if(f.type==="data")(h=f.nodes)==null||h.forEach(_=>{(_==null?void 0:_.type)==="data"&&(_.uses=it(_.uses),_.data=l(_.data))}),o(f);else if(f.type==="chunk"){const{id:_,data:b,error:A}=f,w=i.get(_);i.delete(_),A?w.reject(l(A)):w.fulfil(l(b))}}}})}function it(e){return{dependencies:new Set((e==null?void 0:e.dependencies)??[]),params:new Set((e==null?void 0:e.params)??[]),parent:!!(e!=null&&e.parent),route:!!(e!=null&&e.route),url:!!(e!=null&&e.url),search_params:new Set((e==null?void 0:e.search_params)??[])}}function on(){const e=document.querySelector("[autofocus]");if(e)e.focus();else{const n=document.body,t=n.getAttribute("tabindex");n.tabIndex=-1,n.focus({preventScroll:!0,focusVisible:!1}),t!==null?n.setAttribute("tabindex",t):n.removeAttribute("tabindex");const a=getSelection();if(a&&a.type!=="None"){const r=[];for(let o=0;o<a.rangeCount;o+=1)r.push(a.getRangeAt(o));setTimeout(()=>{if(a.rangeCount===r.length){for(let o=0;o<a.rangeCount;o+=1){const i=r[o],s=a.getRangeAt(o);if(i.commonAncestorContainer!==s.commonAncestorContainer||i.startContainer!==s.startContainer||i.endContainer!==s.endContainer||i.startOffset!==s.startOffset||i.endOffset!==s.endOffset)return}a.removeAllRanges()}})}}}function ct(e,n,t,a){var c,l;let r,o;const i=new Promise((u,h)=>{r=u,o=h});return i.catch(()=>{}),{navigation:{from:{params:e.params,route:{id:((c=e.route)==null?void 0:c.id)??null},url:e.url},to:t&&{params:(n==null?void 0:n.params)??null,route:{id:((l=n==null?void 0:n.route)==null?void 0:l.id)??null},url:t},willUnload:!n,type:a,complete:i},fulfil:r,reject:o}}export{Ye as R,ln as a,P as b,cn as d,nn as g,T as s,re as w};
