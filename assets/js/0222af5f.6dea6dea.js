"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[878],{3905:(t,e,n)=>{n.d(e,{Zo:()=>u,kt:()=>h});var a=n(7294);function i(t,e,n){return e in t?Object.defineProperty(t,e,{value:n,enumerable:!0,configurable:!0,writable:!0}):t[e]=n,t}function o(t,e){var n=Object.keys(t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(t);e&&(a=a.filter((function(e){return Object.getOwnPropertyDescriptor(t,e).enumerable}))),n.push.apply(n,a)}return n}function r(t){for(var e=1;e<arguments.length;e++){var n=null!=arguments[e]?arguments[e]:{};e%2?o(Object(n),!0).forEach((function(e){i(t,e,n[e])})):Object.getOwnPropertyDescriptors?Object.defineProperties(t,Object.getOwnPropertyDescriptors(n)):o(Object(n)).forEach((function(e){Object.defineProperty(t,e,Object.getOwnPropertyDescriptor(n,e))}))}return t}function s(t,e){if(null==t)return{};var n,a,i=function(t,e){if(null==t)return{};var n,a,i={},o=Object.keys(t);for(a=0;a<o.length;a++)n=o[a],e.indexOf(n)>=0||(i[n]=t[n]);return i}(t,e);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(t);for(a=0;a<o.length;a++)n=o[a],e.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(t,n)&&(i[n]=t[n])}return i}var l=a.createContext({}),c=function(t){var e=a.useContext(l),n=e;return t&&(n="function"==typeof t?t(e):r(r({},e),t)),n},u=function(t){var e=c(t.components);return a.createElement(l.Provider,{value:e},t.children)},p={inlineCode:"code",wrapper:function(t){var e=t.children;return a.createElement(a.Fragment,{},e)}},d=a.forwardRef((function(t,e){var n=t.components,i=t.mdxType,o=t.originalType,l=t.parentName,u=s(t,["components","mdxType","originalType","parentName"]),d=c(n),h=i,g=d["".concat(l,".").concat(h)]||d[h]||p[h]||o;return n?a.createElement(g,r(r({ref:e},u),{},{components:n})):a.createElement(g,r({ref:e},u))}));function h(t,e){var n=arguments,i=e&&e.mdxType;if("string"==typeof t||i){var o=n.length,r=new Array(o);r[0]=d;var s={};for(var l in e)hasOwnProperty.call(e,l)&&(s[l]=e[l]);s.originalType=t,s.mdxType="string"==typeof t?t:i,r[1]=s;for(var c=2;c<o;c++)r[c]=n[c];return a.createElement.apply(null,r)}return a.createElement.apply(null,n)}d.displayName="MDXCreateElement"},828:(t,e,n)=>{n.r(e),n.d(e,{assets:()=>l,contentTitle:()=>r,default:()=>p,frontMatter:()=>o,metadata:()=>s,toc:()=>c});var a=n(7462),i=(n(7294),n(3905));const o={id:"installation",title:"Manage Installations",sidebar_position:3},r=void 0,s={unversionedId:"guides/api_actions/installation",id:"guides/api_actions/installation",title:"Manage Installations",description:"This is a guide that refers to an Installation. An Installation refers to a specific instance or part of a resource/service that is allocated to a specific Project by one Provider. If you are permitted to act on one or ore Installations, via this guide you can see all the options you have .",source:"@site/docs/guides/api_actions/installations.md",sourceDirName:"guides/api_actions",slug:"/guides/api_actions/installation",permalink:"/argo-accounting/docs/guides/api_actions/installation",draft:!1,tags:[],version:"current",sidebarPosition:3,frontMatter:{id:"installation",title:"Manage Installations",sidebar_position:3},sidebar:"tutorialSidebar",previous:{title:"Manage Providers",permalink:"/argo-accounting/docs/guides/api_actions/provider"},next:{title:"Manage Metric Definitions",permalink:"/argo-accounting/docs/guides/api_actions/metric_definition"}},l={},c=[{value:"Before you start",id:"before-you-start",level:3},{value:"OPERATIONS",id:"operations",level:2},{value:"GET Installation&#39;s details",id:"get-installations-details",level:3},{value:"UPDATE the Installation",id:"update-the-installation",level:3},{value:"DELETE the Installation",id:"delete-the-installation",level:3}],u={toc:c};function p(t){let{components:e,...n}=t;return(0,i.kt)("wrapper",(0,a.Z)({},u,n,{components:e,mdxType:"MDXLayout"}),(0,i.kt)("p",null,"This is a guide that refers to an Installation. An Installation refers to a specific instance or part of a resource/service that is allocated to a specific Project by one Provider. If you are permitted to act on one or ore Installations, via this guide you can see all the options you have ."),(0,i.kt)("h3",{id:"before-you-start"},"Before you start"),(0,i.kt)("p",null,"You can manage an Installation assigned to a specific Project and Provider.",(0,i.kt)("br",null),"\n",(0,i.kt)("strong",{parentName:"p"},"1.")," Register to the Accounting Service.",(0,i.kt)("br",null),"\n",(0,i.kt)("strong",{parentName:"p"},"2.")," Contact the administrator of the Project or the administrator of the Project's Provider,that this Installation is associated with,to assign you one or more roles on the Installation."),(0,i.kt)("p",null,(0,i.kt)("strong",{parentName:"p"},"\u039d\u039f\u03a4\u0395")," ",(0,i.kt)("br",null),"\nIn the Accounting Service, the ",(0,i.kt)("strong",{parentName:"p"},(0,i.kt)("em",{parentName:"strong"},"installation_admin"))," role is the main role for managing an Installation.This role permits the user to perform any operation,on a specific Installation.\nIn case the user is assigned with any other role, he can operate according to the role's permissions."),(0,i.kt)("h2",{id:"operations"},"OPERATIONS"),(0,i.kt)("hr",null),(0,i.kt)("h3",{id:"get-installations-details"},"GET Installation's details"),(0,i.kt)("details",null,"You can get the details of the Installation.Apply a request to the Accounting Service API.",(0,i.kt)("b",null," For more details,how to syntax the request,see ",(0,i.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/installation#get---fetch-an-existing-installation"},"here"))),(0,i.kt)("h3",{id:"update-the-installation"},"UPDATE the Installation"),(0,i.kt)("details",null,"You can update the Installation.Apply a request to the Accounting Service API,providing the new values of the Installation's properties.",(0,i.kt)("b",null," For more details,how to syntax the request,see ",(0,i.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/installation#patch---update-an-existing-installation"},"here."))),(0,i.kt)("h3",{id:"delete-the-installation"},"DELETE the Installation"),(0,i.kt)("details",null,"You can delete the Installation.Apply a request to the Accounting Service API.If Metrics are assigned to the Installation,no DELETE action can take place.In this case,you need to delete all the assigned Metrics.",(0,i.kt)("b",null," For more details,how to syntax the request,see ",(0,i.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/installation#delete---delete-an-existing-installation"},"here."))),(0,i.kt)("hr",null))}p.isMDXComponent=!0}}]);