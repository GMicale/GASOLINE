import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Vector;

public class FileManager
{
	public Graph[] readGraphs(String path)
	{
		File f=new File(path);
		File[] list=f.listFiles();
		Graph[] g=new Graph[list.length];
		int i=0;
		for(i=0;i<list.length;i++)
		{
			g[i]=new Graph(list[i].getName().toUpperCase());
			try
			{
				BufferedReader br=new BufferedReader(new FileReader(list[i]));
				br.readLine();
				String str="";
				while((str=br.readLine())!=null)
				{
					String[] split=str.split("\t");
					String idSource=split[0];
					String idDest=split[1];
					double weight=Double.parseDouble(split[2]);
					g[i].addNode(idSource);
					g[i].addNode(idDest);
					g[i].addArc(idSource,idDest,weight);
				}
				br.close();
			}
			catch(Exception e)
			{}
			//System.out.println(setLabels[i]);
		}
		return g;
	}
	
	public HashMap<String,Double> readHomologyScores(String homologyFile)
	{
		int i=0;
		HashMap<String,Double> mapHomology=new HashMap<String,Double>();
		try
		{
			BufferedReader br=new BufferedReader(new FileReader(homologyFile));
			String str="";
			while((str=br.readLine())!=null)
			{
				String[] split=str.split("\t");
				String idSource=split[0];
				String idDest=split[1];
				double weight=Double.parseDouble(split[2]);
				if(weight==0.0)
					weight=2000;
				else
					weight=-(Math.log(weight)/Math.log(2));
				mapHomology.put(idSource+"-"+idDest,weight);
				mapHomology.put(idDest+"-"+idSource,weight);
			}
			br.close();
		}
		catch(Exception e)
		{}
		return mapHomology;
	}
	
	public static void writeAlignments(Graph[] g, OrderedList<Alignment> rankAlign, String pathOutput)
	{
		if(pathOutput!=null)
		{
			File f=new File(pathOutput);
			if(!f.exists())
				f.mkdir();
		}
		//System.out.println("\nBEST_ALIGNMENTS:");
		NodeOrdList<Alignment> aux=rankAlign.getMax();
		int cont=1;
		int i=0, j=0, k=0;
		while(aux!=null)
		{
			Alignment a=aux.getInfo();
			//System.out.println(a);
			Vector<String>[] mapping=a.getMapping();
			//Memorizzazione allineamento
			if(pathOutput!=null)
			{
				try
				{
					BufferedWriter bw=new BufferedWriter(new FileWriter(pathOutput+"/Alignment"+cont+".txt"));
					bw.write("Alignment score: "+a.getAlignSize()*a.getIscScore()+"\r\n");
					bw.write("Alignment size: "+a.getAlignSize()+"\r\n");
					bw.write("Index of Structural Conservation (ISC): "+a.getIscScore()+"\r\n"+"\r\n");
					for(j=0;j<mapping.length;j++)
					{
						bw.write(g[j].getName()+":\r\n");
						bw.write(g[j].buildInducedSubgraph(mapping[j])+"\r\n");
					}
					bw.write("MAPPING:\r\n"+"\r\n");
					for(j=0;j<mapping[0].size();j++)
					{
						for(i=0;i<mapping.length-1;i++)
							bw.write(mapping[i].get(j)+"\t");
						bw.write(mapping[i].get(j));
						bw.write("\r\n");
					}
					bw.close();
				}
				catch(Exception e)
				{}
			}
			aux=aux.getNext();
			cont++;
		}
		//System.out.println();
	}
}