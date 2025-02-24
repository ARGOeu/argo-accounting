"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[5528],{2363:(e,i,n)=>{n.r(i),n.d(i,{assets:()=>a,contentTitle:()=>o,default:()=>h,frontMatter:()=>r,metadata:()=>c,toc:()=>l});var t=n(4848),s=n(8453);const r={id:"project_admin",title:"Project Admin",sidebar_position:3},o="Project Admin",c={id:"guides/project_admin",title:"Project Admin",description:"Before you start",source:"@site/docs/guides/project_admin.md",sourceDirName:"guides",slug:"/guides/project_admin",permalink:"/argo-accounting/docs/guides/project_admin",draft:!1,unlisted:!1,tags:[],version:"current",sidebarPosition:3,frontMatter:{id:"project_admin",title:"Project Admin",sidebar_position:3},sidebar:"tutorialSidebar",previous:{title:"Setting up a Project",permalink:"/argo-accounting/docs/guides/setting_up_a_project"},next:{title:"Provider Admin",permalink:"/argo-accounting/docs/guides/provider_admin"}},a={},l=[{value:"Before you start",id:"before-you-start",level:2},{value:"Actions",id:"actions",level:2},{value:"VIEW all Projects that are assigned to you",id:"view-all-projects-that-are-assigned-to-you",level:3},{value:"ASSOCIATE one or more Providers with a specific Project",id:"associate-one-or-more-providers-with-a-specific-project",level:3},{value:"DISSOCIATE Provider from a specific Project",id:"dissociate-provider-from-a-specific-project",level:3},{value:"Create a new Installation on a specific Project",id:"create-a-new-installation-on-a-specific-project",level:3},{value:"Update the Installations belonging to a specific Project",id:"update-the-installations-belonging-to-a-specific-project",level:3},{value:"Delete the Installations belonging to a specific Project",id:"delete-the-installations-belonging-to-a-specific-project",level:3},{value:"Collect Metrics from a specific Project",id:"collect-metrics-from-a-specific-project",level:3},{value:"Manage Metric Definitions, Providers, Unit Types, and Metric Types",id:"manage-metric-definitions-providers-unit-types-and-metric-types",level:3}];function d(e){const i={a:"a",em:"em",h1:"h1",h2:"h2",h3:"h3",hr:"hr",li:"li",p:"p",strong:"strong",ul:"ul",...(0,s.R)(),...e.components};return(0,t.jsxs)(t.Fragment,{children:[(0,t.jsx)(i.h1,{id:"project-admin",children:"Project Admin"}),"\n",(0,t.jsx)(i.h2,{id:"before-you-start",children:"Before you start"}),"\n",(0,t.jsxs)(i.p,{children:[(0,t.jsx)(i.strong,{children:"1."})," ",(0,t.jsx)(i.a,{href:"/argo-accounting/docs/guides/registration",children:"Register"})," to Accounting Service."]}),"\n",(0,t.jsxs)(i.p,{children:[(0,t.jsx)(i.strong,{children:"2."})," ",(0,t.jsx)(i.a,{href:"/argo-accounting/docs/authorization/assigning_roles",children:"Contact"})," the system\nadministrator, to assign you the Project Admin role upon the project\nyou want."]}),"\n",(0,t.jsxs)(i.p,{children:["In the Accounting Service, the ",(0,t.jsx)(i.strong,{children:(0,t.jsx)(i.em,{children:"project_admin"})})," role is the main role\nfor managing a Project. This role permits the client to perform any operation,\non a specific Project."]}),"\n",(0,t.jsxs)(i.p,{children:["Below we describe the actions a ",(0,t.jsx)(i.strong,{children:(0,t.jsx)(i.em,{children:"project_admin"})})," can either perform through\nthe Accounting User Interface or a simple HTTP request."]}),"\n",(0,t.jsx)(i.h2,{id:"actions",children:"Actions"}),"\n",(0,t.jsx)(i.h3,{id:"view-all-projects-that-are-assigned-to-you",children:"VIEW all Projects that are assigned to you"}),"\n",(0,t.jsx)(i.hr,{}),"\n",(0,t.jsxs)(i.ul,{children:["\n",(0,t.jsxs)(i.li,{children:["\n",(0,t.jsxs)(i.p,{children:[(0,t.jsx)(i.strong,{children:"User Interface"}),"\nTo perform this action via the website, please click ",(0,t.jsx)(i.a,{href:"https://accounting.eosc-portal.eu/projects",children:"here"}),"."]}),"\n"]}),"\n",(0,t.jsxs)(i.li,{children:["\n",(0,t.jsxs)(i.p,{children:[(0,t.jsx)(i.strong,{children:"HTTP Request"}),"\nTo syntax the HTTP request, please visit the corresponding ",(0,t.jsx)(i.a,{href:"https://argoeu.github.io/argo-accounting/docs/api/project#get---fetch-all-projects",children:"document"}),"."]}),"\n"]}),"\n"]}),"\n",(0,t.jsx)(i.h3,{id:"associate-one-or-more-providers-with-a-specific-project",children:"ASSOCIATE one or more Providers with a specific Project"}),"\n",(0,t.jsx)(i.hr,{}),"\n",(0,t.jsxs)(i.ul,{children:["\n",(0,t.jsxs)(i.li,{children:["\n",(0,t.jsxs)(i.p,{children:[(0,t.jsx)(i.strong,{children:"User Interface"}),"\nTo perform this action via the website, please click ",(0,t.jsx)(i.a,{href:"https://accounting.eosc-portal.eu/projects",children:"here"}),"\nand follow the provided ",(0,t.jsx)(i.a,{href:"https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/project#associate-providers-with-a-specific-project",children:"instructions"}),"."]}),"\n"]}),"\n",(0,t.jsxs)(i.li,{children:["\n",(0,t.jsxs)(i.p,{children:[(0,t.jsx)(i.strong,{children:"HTTP Request"}),"\nTo syntax the HTTP request, please visit the corresponding ",(0,t.jsx)(i.a,{href:"https://argoeu.github.io/argo-accounting/docs/api/project#post---associate-providers-with-a-specific-project",children:"document"}),"."]}),"\n"]}),"\n"]}),"\n",(0,t.jsx)(i.h3,{id:"dissociate-provider-from-a-specific-project",children:"DISSOCIATE Provider from a specific Project"}),"\n",(0,t.jsx)(i.hr,{}),"\n",(0,t.jsxs)(i.ul,{children:["\n",(0,t.jsxs)(i.li,{children:["\n",(0,t.jsxs)(i.p,{children:[(0,t.jsx)(i.strong,{children:"User Interface"}),"\nTo perform this action via the website, please click ",(0,t.jsx)(i.a,{href:"https://accounting.eosc-portal.eu/projects",children:"here"}),"\nand follow the provided ",(0,t.jsx)(i.a,{href:"https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/project/#dissociate-providers-from-a-specific-project",children:"instructions"}),"."]}),"\n"]}),"\n",(0,t.jsxs)(i.li,{children:["\n",(0,t.jsxs)(i.p,{children:[(0,t.jsx)(i.strong,{children:"HTTP Request"}),"\nTo syntax the HTTP request, please visit the corresponding ",(0,t.jsx)(i.a,{href:"https://argoeu.github.io/argo-accounting/docs/api/project#post---dissociate-providers-from-a-project",children:"document"}),"."]}),"\n"]}),"\n"]}),"\n",(0,t.jsx)(i.h3,{id:"create-a-new-installation-on-a-specific-project",children:"Create a new Installation on a specific Project"}),"\n",(0,t.jsx)(i.hr,{}),"\n",(0,t.jsxs)(i.ul,{children:["\n",(0,t.jsxs)(i.li,{children:["\n",(0,t.jsxs)(i.p,{children:[(0,t.jsx)(i.strong,{children:"User Interface"}),"\nTo perform this action via the website, please click ",(0,t.jsx)(i.a,{href:"https://accounting.eosc-portal.eu/installations",children:"here"}),"\nand follow the provided ",(0,t.jsx)(i.a,{href:"https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#create-a-new-installation",children:"instructions"}),"."]}),"\n"]}),"\n",(0,t.jsxs)(i.li,{children:["\n",(0,t.jsxs)(i.p,{children:[(0,t.jsx)(i.strong,{children:"HTTP Request"}),"\nTo syntax the HTTP request, please visit the corresponding ",(0,t.jsx)(i.a,{href:"https://argoeu.github.io/argo-accounting/docs/api/installation#post---create-a-new-installation",children:"document"}),"."]}),"\n"]}),"\n"]}),"\n",(0,t.jsx)(i.h3,{id:"update-the-installations-belonging-to-a-specific-project",children:"Update the Installations belonging to a specific Project"}),"\n",(0,t.jsx)(i.hr,{}),"\n",(0,t.jsxs)(i.ul,{children:["\n",(0,t.jsxs)(i.li,{children:["\n",(0,t.jsxs)(i.p,{children:[(0,t.jsx)(i.strong,{children:"User Interface"}),"\nTo perform this action via the website, please click ",(0,t.jsx)(i.a,{href:"https://accounting.eosc-portal.eu/installations",children:"here"}),"\nand follow the provided ",(0,t.jsx)(i.a,{href:"https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#update-an-existing-installation",children:"instructions"}),"."]}),"\n"]}),"\n",(0,t.jsxs)(i.li,{children:["\n",(0,t.jsxs)(i.p,{children:[(0,t.jsx)(i.strong,{children:"HTTP Request"}),"\nTo syntax the HTTP request, please visit the corresponding ",(0,t.jsx)(i.a,{href:"https://argoeu.github.io/argo-accounting/docs/api/installation#patch---update-an-existing-installation",children:"document"}),"."]}),"\n"]}),"\n"]}),"\n",(0,t.jsx)(i.h3,{id:"delete-the-installations-belonging-to-a-specific-project",children:"Delete the Installations belonging to a specific Project"}),"\n",(0,t.jsx)(i.hr,{}),"\n",(0,t.jsxs)(i.ul,{children:["\n",(0,t.jsxs)(i.li,{children:["\n",(0,t.jsxs)(i.p,{children:[(0,t.jsx)(i.strong,{children:"User Interface"}),"\nTo perform this action via the website, please click ",(0,t.jsx)(i.a,{href:"https://accounting.eosc-portal.eu/installations",children:"here"}),"\nand follow the provided ",(0,t.jsx)(i.a,{href:"https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#delete-an-existing-installation",children:"instructions"}),"."]}),"\n"]}),"\n",(0,t.jsxs)(i.li,{children:["\n",(0,t.jsxs)(i.p,{children:[(0,t.jsx)(i.strong,{children:"HTTP Request"}),"\nTo syntax the HTTP request, please visit the corresponding ",(0,t.jsx)(i.a,{href:"https://argoeu.github.io/argo-accounting/docs/api/installation#delete---delete-an-existing-installation",children:"document"}),"."]}),"\n"]}),"\n"]}),"\n",(0,t.jsx)(i.h3,{id:"collect-metrics-from-a-specific-project",children:"Collect Metrics from a specific Project"}),"\n",(0,t.jsx)(i.hr,{}),"\n",(0,t.jsxs)(i.ul,{children:["\n",(0,t.jsxs)(i.li,{children:["\n",(0,t.jsxs)(i.p,{children:[(0,t.jsx)(i.strong,{children:"User Interface"}),"\nTo perform this action via the website, please click ",(0,t.jsx)(i.a,{href:"https://accounting.eosc-portal.eu/projects",children:"here"}),"\nand follow the provided ",(0,t.jsx)(i.a,{href:"https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/project#collect-metrics-from-specific-project",children:"instructions"}),"."]}),"\n"]}),"\n",(0,t.jsxs)(i.li,{children:["\n",(0,t.jsxs)(i.p,{children:[(0,t.jsx)(i.strong,{children:"HTTP Request"}),"\nTo syntax the HTTP request, please visit the corresponding ",(0,t.jsx)(i.a,{href:"https://argoeu.github.io/argo-accounting/docs/api/collect_metrics#get---collecting-metrics-from-specific-project",children:"document"}),"."]}),"\n"]}),"\n"]}),"\n",(0,t.jsx)(i.h3,{id:"manage-metric-definitions-providers-unit-types-and-metric-types",children:"Manage Metric Definitions, Providers, Unit Types, and Metric Types"}),"\n",(0,t.jsx)(i.hr,{}),"\n",(0,t.jsxs)(i.p,{children:["As a ",(0,t.jsx)(i.strong,{children:(0,t.jsx)(i.em,{children:"project_admin"})}),", you can create, update, and delete:"]}),"\n",(0,t.jsxs)(i.ul,{children:["\n",(0,t.jsxs)(i.li,{children:[(0,t.jsx)(i.strong,{children:"Metric Definitions"}),": ",(0,t.jsx)(i.a,{href:"https://accounting.eosc-portal.eu/metrics-definitions",children:"Manage Metric Definitions"})]}),"\n",(0,t.jsxs)(i.li,{children:[(0,t.jsx)(i.strong,{children:"Providers"}),": ",(0,t.jsx)(i.a,{href:"https://accounting.eosc-portal.eu/providers",children:"Manage Providers"})]}),"\n",(0,t.jsxs)(i.li,{children:[(0,t.jsx)(i.strong,{children:"Unit Types"}),": ",(0,t.jsx)(i.a,{href:"https://accounting.eosc-portal.eu/unit-types",children:"Manage Unit Types"})]}),"\n",(0,t.jsxs)(i.li,{children:[(0,t.jsx)(i.strong,{children:"Metric Types"}),": ",(0,t.jsx)(i.a,{href:"https://accounting.eosc-portal.eu/metric-types",children:"Manage Metric Types"})]}),"\n"]}),"\n",(0,t.jsxs)(i.p,{children:["Each of these actions has a corresponding ",(0,t.jsx)(i.a,{href:"https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/",children:"guide"}),"\nand ",(0,t.jsx)(i.a,{href:"https://argoeu.github.io/argo-accounting/docs/api/",children:"API documentation"}),"."]}),"\n",(0,t.jsxs)(i.p,{children:["Please note that you can perform all the actions on ",(0,t.jsx)(i.a,{href:"/argo-accounting/docs/guides/provider_admin",children:"Providers"}),"\nand ",(0,t.jsx)(i.a,{href:"/argo-accounting/docs/guides/installation_admin",children:"Installations"})," belonging to the\nProject you have been granted as a ",(0,t.jsx)(i.strong,{children:(0,t.jsx)(i.em,{children:"project_admin"})}),"."]}),"\n",(0,t.jsx)(i.hr,{})]})}function h(e={}){const{wrapper:i}={...(0,s.R)(),...e.components};return i?(0,t.jsx)(i,{...e,children:(0,t.jsx)(d,{...e})}):d(e)}},8453:(e,i,n)=>{n.d(i,{R:()=>o,x:()=>c});var t=n(6540);const s={},r=t.createContext(s);function o(e){const i=t.useContext(r);return t.useMemo((function(){return"function"==typeof e?e(i):{...i,...e}}),[i,e])}function c(e){let i;return i=e.disableParentContext?"function"==typeof e.components?e.components(s):e.components||s:o(e.components),t.createElement(r.Provider,{value:i},e.children)}}}]);