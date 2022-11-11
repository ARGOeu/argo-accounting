"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[243],{3905:(e,t,r)=>{r.d(t,{Zo:()=>u,kt:()=>d});var o=r(7294);function i(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function a(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);t&&(o=o.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,o)}return r}function c(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?a(Object(r),!0).forEach((function(t){i(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):a(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function n(e,t){if(null==e)return{};var r,o,i=function(e,t){if(null==e)return{};var r,o,i={},a=Object.keys(e);for(o=0;o<a.length;o++)r=a[o],t.indexOf(r)>=0||(i[r]=e[r]);return i}(e,t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(o=0;o<a.length;o++)r=a[o],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(i[r]=e[r])}return i}var s=o.createContext({}),l=function(e){var t=o.useContext(s),r=t;return e&&(r="function"==typeof e?e(t):c(c({},t),e)),r},u=function(e){var t=l(e.components);return o.createElement(s.Provider,{value:t},e.children)},p={inlineCode:"code",wrapper:function(e){var t=e.children;return o.createElement(o.Fragment,{},t)}},h=o.forwardRef((function(e,t){var r=e.components,i=e.mdxType,a=e.originalType,s=e.parentName,u=n(e,["components","mdxType","originalType","parentName"]),h=l(r),d=i,g=h["".concat(s,".").concat(d)]||h[d]||p[d]||a;return r?o.createElement(g,c(c({ref:t},u),{},{components:r})):o.createElement(g,c({ref:t},u))}));function d(e,t){var r=arguments,i=t&&t.mdxType;if("string"==typeof e||i){var a=r.length,c=new Array(a);c[0]=h;var n={};for(var s in t)hasOwnProperty.call(t,s)&&(n[s]=t[s]);n.originalType=e,n.mdxType="string"==typeof e?e:i,c[1]=n;for(var l=2;l<a;l++)c[l]=r[l];return o.createElement.apply(null,c)}return o.createElement.apply(null,r)}h.displayName="MDXCreateElement"},7764:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>s,contentTitle:()=>c,default:()=>p,frontMatter:()=>a,metadata:()=>n,toc:()=>l});var o=r(7462),i=(r(7294),r(3905));const a={id:"project",title:"Manage Projects",sidebar_position:1},c=void 0,n={unversionedId:"guides/api_actions/project",id:"guides/api_actions/project",title:"Manage Projects",description:"This is a guide that refers to a Project.",source:"@site/docs/guides/api_actions/projects.md",sourceDirName:"guides/api_actions",slug:"/guides/api_actions/project",permalink:"/argo-accounting/docs/guides/api_actions/project",draft:!1,tags:[],version:"current",sidebarPosition:1,frontMatter:{id:"project",title:"Manage Projects",sidebar_position:1},sidebar:"tutorialSidebar",previous:{title:"API Actions",permalink:"/argo-accounting/docs/category/api-actions"},next:{title:"Manage Providers",permalink:"/argo-accounting/docs/guides/api_actions/provider"}},s={},l=[{value:"Before you start",id:"before-you-start",level:3},{value:"OPERATIONS",id:"operations",level:2},{value:"GET Project&#39;s Hierarchy details",id:"get-projects-hierarchy-details",level:3},{value:"ASSOCIATE one or more Providers with a specific Project",id:"associate-one-or-more-providers-with-a-specific-project",level:3},{value:"DISSOCIATE Provider from a specific Project",id:"dissociate-provider-from-a-specific-project",level:3},{value:"FETCH Project&#39;s hierarchical structure",id:"fetch-projects-hierarchical-structure",level:3},{value:"FETCH all Projects",id:"fetch-all-projects",level:3},{value:"Provide access roles on the Project",id:"provide-access-roles-on-the-project",level:3},{value:"SEARCH Projects",id:"search-projects",level:3}],u={toc:l};function p(e){let{components:t,...r}=e;return(0,i.kt)("wrapper",(0,o.Z)({},u,r,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("p",null,"This is a guide that refers to a Project.\nA Project is the main resource of the Accounting System.\nIf you are permitted to act on one or more Projects, via this guide you can see all the options you have ."),(0,i.kt)("h3",{id:"before-you-start"},"Before you start"),(0,i.kt)("p",null,"You can manage a Project.",(0,i.kt)("br",null),"\n",(0,i.kt)("strong",{parentName:"p"},"1.")," Register to Accounting Service.",(0,i.kt)("br",null),"\n",(0,i.kt)("strong",{parentName:"p"},"2.")," Contact the system administrator, to assign you one or more roles on the Project. "),(0,i.kt)("p",null,(0,i.kt)("strong",{parentName:"p"},"\u039d\u039f\u03a4\u0395")," ",(0,i.kt)("br",null),"\nIn the Accounting Service, the ",(0,i.kt)("strong",{parentName:"p"},(0,i.kt)("em",{parentName:"strong"},"project_admin"))," role is the main role for managing a Project.This role permits the user to perform any operation,on a specific Project.\nIn case the user is assigned with any other role,he can operate according to the role's permissions."),(0,i.kt)("h2",{id:"operations"},"OPERATIONS"),(0,i.kt)("hr",null),(0,i.kt)("h3",{id:"get-projects-hierarchy-details"},"GET Project's Hierarchy details"),(0,i.kt)("details",null,"You can get the detais of the Project's structuure,to retrieve the Providers and Installations associated with the Project.Apply a request to the Accounting Service API.",(0,i.kt)("b",null," For more details,how to syntax the request,see ",(0,i.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/project#get---project-hierarchical-structure"},"here"))),(0,i.kt)("h3",{id:"associate-one-or-more-providers-with-a-specific-project"},"ASSOCIATE one or more Providers with a specific Project"),(0,i.kt)("details",null,"You can associate one or more Providers with a specific Project.This is a required action,in order,to be able to assign Installations and Metrics.Apply a request to the Accounting Service API.",(0,i.kt)("b",null," For more details,how to syntax the request,see ",(0,i.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/project#post---associate-providers-with-a-specific-project"},"here."))),(0,i.kt)("h3",{id:"dissociate-provider-from-a-specific-project"},"DISSOCIATE Provider from a specific Project"),(0,i.kt)("details",null,"If a Provider is associated with a specific Project, you can dessociate it.Provider can be dissociated only if no installations are assigned.Apply a request to the Accounting Service API.",(0,i.kt)("b",null," For more details,how to syntax the request,see ",(0,i.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/project#post---dissociate-providers-from-a-project"},"here."))),(0,i.kt)("h3",{id:"fetch-projects-hierarchical-structure"},"FETCH Project's hierarchical structure"),(0,i.kt)("details",null,"You can fetch the hierarchy of the Project (all Providers and Installations that are assigned to the Project). Apply a request to the Accounting Service API.",(0,i.kt)("b",null," For more details,how to syntax the request,see ",(0,i.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/project#get---project-hierarchical-structure"},"here."))),(0,i.kt)("h3",{id:"fetch-all-projects"},"FETCH all Projects"),(0,i.kt)("details",null,"You can fetch all the Projects that are assigned to you.Apply a request to the Accounting Service API.",(0,i.kt)("b",null," For more details,how to syntax the request,see ",(0,i.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/project#get---fetch-all-projects"},"here."))),(0,i.kt)("h3",{id:"provide-access-roles-on-the-project"},"Provide access roles on the Project"),(0,i.kt)("details",null,"You can provide users with access roles on the Project.",(0,i.kt)("br",null),(0,i.kt)("p",null,(0,i.kt)("strong",{parentName:"p"},"1.")," Read registered clients ( see ",(0,i.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/client#get---read-the-registered-clients)"},"here"),") and retrieve client's id.",(0,i.kt)("br",null),"\n",(0,i.kt)("strong",{parentName:"p"},"2.")," Decide one or more roles,that this user will be assigned with,on the Project and apply a request to the Accounting Service API."),(0,i.kt)("b",null," For more details,how to syntax the request,see ",(0,i.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/project#post---access-control-entry-for-a-particular-project"},"here."))),(0,i.kt)("h3",{id:"search-projects"},"SEARCH Projects"),(0,i.kt)("details",null,"If you are assigned with many Projects, you can search for specific Project/Projects,that match one or more criteria.You can define search criteria, on each field of the ",(0,i.kt)("b",null,(0,i.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/project"}," Project Collection"))," or a combination of search criteria on more than one fields.You can search by Project's acronym, title, period, call identifier or a combination of them. Apply a request to the Accounting Service API.You need to provide the search criteria in a specific ",(0,i.kt)("b",null,(0,i.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/guides/search-filter"}," syntax")),".",(0,i.kt)("b",null," For more details,how to syntax the request,see ",(0,i.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/project#post---search-for-projects"},"here."))),(0,i.kt)("p",null,"In the case the role assigned to you,on the Project,is administrative, you can perform all the actions on the Providers,described at this ",(0,i.kt)("b",null,(0,i.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/guides/provider"},"section")),",as well as all the actions on Installations, described at this ",(0,i.kt)("b",null,(0,i.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/guides/installation"},"section")),". "),(0,i.kt)("hr",null))}p.isMDXComponent=!0}}]);