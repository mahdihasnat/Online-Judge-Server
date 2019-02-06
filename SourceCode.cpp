#include<bits/stdc++.h>
using namespace std;
 
typedef pair < int ,int > pii;
 
int vis[30005];
 
 
int far(int u,vector < pii > *edge)
{
    //cout<<" far from  "<<u<<endl;
    memset(vis,0,sizeof vis);
    queue <pii > q;
    q.push({u,0});
    pii ans({u,0});
    vis[u]=1;
    //cout<<" ans= "<<ans.first<<" "<<ans.second<<endl;
    while(!q.empty())
    {
        //cout<<" haha ";
        pii uu=q.front();
        q.pop();
        if(ans.second<uu.second)
        {
            ans=uu;
        }
        //cout<<" ans= "<<ans.first<<" "<<ans.second<<endl;
        for(int i=0;i< edge[uu.first].size();i++)
        {
            pii x=edge[uu.first][i];
            if(vis[x.first]) continue;
            vis[x.first]=1;
            q.push({x.first,uu.second+x.second});
        }
 
    }
    return ans.first;
}
void save(int u,int *arr,vector < pii > *edge)
{
 
    memset(vis,0,sizeof vis);
    queue <pii > q;
    q.push({u,0});
    vis[u]=1;
    while(!q.empty())
    {
        //cout<<" haha ";
        pii uu=q.front();
        q.pop();
        arr[uu.first]=uu.second;
        for(int i=0;i< edge[uu.first].size();i++)
        {
            pii x=edge[uu.first][i];
            if(vis[x.first]) continue;
            vis[x.first]=1;
            q.push({x.first,uu.second+x.second});
        }
    }
    return ;
}
 
int main()
{
    ios::sync_with_stdio(false);cin.tie(0);cout.tie(0);
    int t,cs=0;
    scanf(" %d",&t);
    //cin>>t;
    while(t--)
    {
        int n;
 
        //cin>>n;
        scanf(" %d",&n);
        vector < pii > edge[n+1];
        for(int i=1;i< n;i++)
        {
            int u,v,w;
            //cin>>u>>v>>w;
            scanf(" %d %d %d",&u,&v,&w);
            edge[u].push_back({v,w});
            edge[v].push_back({u,w});
        }
        int n2=far(0,edge);
        int n3=far(n2,edge);
        int n4=far(n3,edge);
        int arr1[n+1];
        int arr2[n+1];
        save(n3,arr1,edge);
        save(n4,arr2,edge);
        //cout<<"n2 = "<<n2<<" n3 = "<<n3<<" n4 = "<<n4<<endl;
        //cout<<"Case "<<++cs<<":\n";
        printf("Case %d:\n",++cs);
        for(int i=0;i<n;i++)
        {
            //out<<"1= "<<arr1[i]<<" 2 =  "<<arr2[i]<<endl;
            //cout<<<<endl;;
            printf("%d\n",max(arr1[i],arr2[i]));
        }
        for(int i=0;i<=n;i++)
        {
            edge[i].erase(edge[i].begin(),edge[i].end());
        }
    }
}
 
