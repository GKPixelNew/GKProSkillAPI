import{s as S,e as _,t as f,b as q,f as d,i as g,j as h,d as l,k as x,l as m,m as v,p as $,n as E,q as j}from"../chunks/scheduler.D33XR0fH.js";import{S as k,i as y}from"../chunks/index._QfgRVPV.js";import{s as C}from"../chunks/entry.BxgPiiSs.js";const H=()=>{const s=C;return{page:{subscribe:s.page.subscribe},navigating:{subscribe:s.navigating.subscribe},updated:s.updated}},P={subscribe(s){return H().page.subscribe(s)}};function w(s){var b;let t,r=s[0].status+"",o,n,i,p=((b=s[0].error)==null?void 0:b.message)+"",u;return{c(){t=_("h1"),o=f(r),n=q(),i=_("p"),u=f(p)},l(e){t=d(e,"H1",{});var a=g(t);o=h(a,r),a.forEach(l),n=x(e),i=d(e,"P",{});var c=g(i);u=h(c,p),c.forEach(l)},m(e,a){m(e,t,a),v(t,o),m(e,n,a),m(e,i,a),v(i,u)},p(e,[a]){var c;a&1&&r!==(r=e[0].status+"")&&$(o,r),a&1&&p!==(p=((c=e[0].error)==null?void 0:c.message)+"")&&$(u,p)},i:E,o:E,d(e){e&&(l(t),l(n),l(i))}}}function z(s,t,r){let o;return j(s,P,n=>r(0,o=n)),[o]}let F=class extends k{constructor(t){super(),y(this,t,z,w,S,{})}};export{F as component};
