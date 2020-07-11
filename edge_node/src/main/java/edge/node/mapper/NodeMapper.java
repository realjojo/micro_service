package edge.node.mapper;

import edge.node.model.Node;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NodeMapper {
    @Insert("INSERT INTO node(nodeName,location,nodeStatus,nodeCreateAt,runAt,endLastAt,cpu,memory,ip)" +
            " VALUES(#{nodeName},#{location},#{nodeStatus},#{nodeCreateAt},#{runAt},#{endLastAt},#{cpu},#{cpuRate},#{memory},#{memRate},#{ip})")
      public void create_node(@Param("nodeName")String nodeName, @Param("location")String location,
                            @Param("node_status")boolean node_status,
                            @Param("nodeCreateAt")String nodeCreateAt, @Param("runAt")String runAt,
                            @Param("endLastAt")String endLastAt, @Param("cpu")String cpu,
                              @Param("cpuRate")double cpuRate, @Param("memory")String memory,
                              @Param("memory")double memRate, @Param("ip")String ip);


    /*-------select--------------*/
    @Select("SELECT * FROM node WHERE nodeName=#{nodeName}")
    public Node getNodeByNodeName(@Param("nodeName") String nodeName);

    @Select("SELECT * FROM node")
    public List<Node> get_all();

    @Select("SELECT nodeStatus FROM node WHERE nodeName=#{nodeName}")
    public boolean geNodeStatusByNodeName(@Param("nodeName")String nodeName);

    @Select("SELECT location FROM node")
    public List<String> get_all_location();

    @Select("SELECT location FROM node WHERE nodeStatus=true")
    public List<String> get_on_location();

    @Select("SELECT location FROM node WHERE nodeStatus=false")
    public List<String> get_off_location();

    @Select("SELECT * FROM node WHERE nodeStatus=true")
    public List<Node> get_on_num();

    @Select("SELECT id FROM node WHERE nodeStatus=true")
    public List<Integer> get_node_id();

    @Select("SELECT ip FROM node")
    public List<String> get_node_ip();

    /*-------update------------*/
    @Update("UPDATE node SET nodeStatus=#{nodeStatus} WHERE nodeName=#{nodeName}")
    public void updateNodeStatusByNodeName(@Param("nodeStatus")boolean nodeStatus, @Param("nodeName") String nodeName);

    @Update("UPDATE node SET runAt=#{runAt} WHERE nodeName=#{nodeName}")
    public void updateRunAtByNodeName(@Param("runAt")String runAt,@Param("nodeName")String nodeName);

    @Update("UPDATE node SET endLastAt=#{endLastAt} WHERE nodeName=#{nodeName}")
    public void updateEndLastAtByNodeName(@Param("endLastAt")String run_at,@Param("nodeName")String nodeName);

    /*-------delete-----------*/
    @Delete("DELETE FROM node WHERE nodeName=#{nodeName}")
    public void deleteNodeByNodeName(@Param("nodeName")String nodeName);
}
