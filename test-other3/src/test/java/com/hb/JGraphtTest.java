package com.hb;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.junit.Assert;
import org.junit.Test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import javax.imageio.ImageIO;

import org.jgrapht.*;
import org.jgrapht.generate.*;
import org.jgrapht.graph.*;
import org.jgrapht.traverse.*;
import org.jgrapht.util.*;

import java.util.*;
import java.util.function.*;

/**
 * @author hubin
 * @date 2022年08月08日 14:24
 */
public class JGraphtTest {
    @Test
    public void test1() {
        Graph<String, DefaultEdge> sg = createStringGraph();

        //打印这张图
        System.out.println(sg);
    }

    private static Graph<String, DefaultEdge> createStringGraph() {
        Graph<String, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);

        String v1 = "v1";
        String v2 = "v2";
        String v3 = "v3";
        String v4 = "v4";
        String v5 = "v5";

        // 增加顶点
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);


        // 增加边
        g.addEdge(v1, v2);
        g.addEdge(v2, v3);
        g.addEdge(v2, v4);
        g.addEdge(v4, v5);
        g.addEdge(v2, v5);

        return g;
    }


    /**
     * 测试遍历图
     */
    @Test
    public void testOtherAPI() {
        String a = "A";
        String b = "B";
        String c = "C";
        String d = "D";
        String e = "E";
        String f = "F";

        DefaultDirectedGraph<String, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);

        // 添加顶点
        g.addVertex(a);
        g.addVertex(b);
        g.addVertex(c);
        g.addVertex(d);
        g.addVertex(e);
        g.addVertex(f);

        // 添加边
        g.addEdge(a, b);
        g.addEdge(a, c);
        g.addEdge(b, d);
        g.addEdge(c, e);
        g.addEdge(d, f);
        g.addEdge(e, f);

        // 广度优先来遍历到节点
        // Iterator<String> iterator = new BreadthFirstIterator<>(g, e);
        // while (iterator.hasNext()) {
        //     // System.out.println(iterator.next());
        // }


        // 拓扑排序(按图的层级来排序)
        TopologicalOrderIterator<String, DefaultEdge> veTopologicalOrderIterator = new TopologicalOrderIterator<String, DefaultEdge>(g);
        while (veTopologicalOrderIterator.hasNext()) {
            System.out.println(veTopologicalOrderIterator.next());
        }

    }


    /**
     * 测试最短路径
     */
    @Test
    public void test3() {
        String a = "A";
        String b = "B";
        String c = "C";
        String d = "D";
        String e = "E";
        String f = "F";

        Graph<String, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);

        // 添加顶点
        g.addVertex(a);
        g.addVertex(b);
        g.addVertex(c);
        g.addVertex(d);
        g.addVertex(e);
        g.addVertex(f);

        // 添加边
        g.addEdge(a, b);
        g.addEdge(a, c);
        g.addEdge(b, d);
        g.addEdge(c, e);
        g.addEdge(d, f);
        g.addEdge(e, f);

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(g);
        List vertexList = dijkstraShortestPath.getPath(a, f).getVertexList();
        System.out.println(vertexList);
    }


    /**
     * 测试将graph可视化保存到图片.
     *
     * @throws IOException
     */
    @Test
    public void test5() throws IOException {

        String a = "A";
        String b = "B";
        String c = "C";
        String d = "D";
        String e = "E";
        String f = "F";

        Graph<String, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);

        // 添加顶点
        g.addVertex(a);
        g.addVertex(b);
        g.addVertex(c);
        g.addVertex(d);
        g.addVertex(e);
        g.addVertex(f);

        // 添加边
        g.addEdge(a, b);
        g.addEdge(a, c);
        g.addEdge(b, d);
        g.addEdge(c, e);
        g.addEdge(d, f);
        g.addEdge(e, f);

        JGraphXAdapter<String, DefaultEdge> graphAdapter = new JGraphXAdapter<String, DefaultEdge>(g);
        mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        BufferedImage image = mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        File imgFile = new File("src/test/resources/graph.png");
        ImageIO.write(image, "PNG", imgFile);

        Assert.assertTrue(imgFile.exists());

    }


    /**
     * 查找指定节点的上游 (广度优先遍历)
     */
    @Test
    public void test6() {
        String a = "A";
        String b = "B";
        String c = "C";
        String d = "D";
        String e = "E";
        String f = "F";

        Graph<String, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);

        // 添加顶点
        g.addVertex(a);
        g.addVertex(b);
        g.addVertex(c);
        g.addVertex(d);
        g.addVertex(e);
        g.addVertex(f);

        // 添加边
        g.addEdge(a, b);
        g.addEdge(a, c);
        g.addEdge(b, d);
        g.addEdge(c, e);
        g.addEdge(d, f);
        g.addEdge(e, f);


        // 广度优先来遍历到节点
        Iterator<String> iterator = new BreadthFirstIterator<>(g, c);
        while (iterator.hasNext()) {
            System.out.println(": " + iterator.next());
        }




    }



}
